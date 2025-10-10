package com.lynn.yuaicodemother.service.impl;

import cn.hutool.core.util.StrUtil;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.exception.ThrowUtils;
import com.lynn.yuaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.lynn.yuaicodemother.model.entity.App;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.lynn.yuaicodemother.service.AppService;
import com.lynn.yuaicodemother.service.UserService;
import com.lynn.yuaicodemother.util.MarkdownUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.lynn.yuaicodemother.model.entity.ChatHistory;
import com.lynn.yuaicodemother.mapper.ChatHistoryMapper;
import com.lynn.yuaicodemother.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author Linz
 */
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

    @Resource
    @Lazy
    private AppService appService;

    @Resource
    private UserService userService;

    @Override
    public boolean addChatMessage(Long appId, String message, String messageType, Long userId) {
        // 1.校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID错误");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        ThrowUtils.throwIf(userId == null || userId < 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");

        // 2.验证消息类型是否有效
        ChatHistoryMessageTypeEnum messageTypeEnum = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
        ThrowUtils.throwIf(messageTypeEnum == null, ErrorCode.PARAMS_ERROR, "消息类型错误");

        // 3.保存对话消息
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .message(message)
                .messageType(messageType)
                .userId(userId)
                .build();

        return this.save(chatHistory);
    }

    @Override
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                                      LocalDateTime lastCreateTime,
                                                      User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(pageSize <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "页面大小必须在1-50之间");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 验证权限：所有人都可以查看
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 构建查询条件
        ChatHistoryQueryRequest queryRequest = new ChatHistoryQueryRequest();
        queryRequest.setAppId(appId);
        queryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper queryWrapper = this.getQueryWrapper(queryRequest);
        // 查询数据
        return this.page(Page.of(1, pageSize), queryWrapper);
    }

    @Override
    public boolean deleteByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        return this.remove(new QueryWrapper().eq(ChatHistory::getAppId, appId));
    }

    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount){
        try {

            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(ChatHistory::getAppId, appId)
                    .orderBy(ChatHistory::getCreateTime, false)
                    .limit(1, maxCount); // 从1开始加载是因为在创建appService时已经加了用户发送的历史消息，无需从数据库中再次加载此条消息
            List<ChatHistory> chatHistoryList = this.list(queryWrapper);
            if (chatHistoryList == null){
                return 0;
            }
            // 反转列表，确保按照时间正序（老的在前，新的在后）
            chatHistoryList.reversed();
            // 在添加记忆前，清空记忆，防止重复加载
            chatMemory.clear();
            int loadedCount =  0;
            // 按照时间顺序将消息添加到记忆中
            for (ChatHistory chatHistory : chatHistoryList){
                if (chatHistory.getMessageType().equals(ChatHistoryMessageTypeEnum.USER.getValue())){
                    // 用户消息
                    chatMemory.add(UserMessage.from(chatHistory.getMessage()));

                }else if (chatHistory.getMessageType().equals(ChatHistoryMessageTypeEnum.AI.getValue())){
                    // AI消息
                    chatMemory.add(AiMessage.from(chatHistory.getMessage()));

                }
                loadedCount++;
            }
            log.info("成功为 appId：{} 加载 {} 条历史消息", appId, loadedCount);
            return loadedCount; // 返回加载的记录数
        }catch (Exception e) {
            log.error("加载历史对话失败，appId: {}，error：{}", appId, e.getMessage(),e);
            return 0; // 返回加载失败的记录数
        }
    }


    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (chatHistoryQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chatHistoryQueryRequest.getId();
        String message = chatHistoryQueryRequest.getMessage();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long appId = chatHistoryQueryRequest.getAppId();
        Long userId = chatHistoryQueryRequest.getUserId();
        LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq("id", id)
                .like("message", message)
                .eq("messageType", messageType)
                .eq("appId", appId)
                .eq("userId", userId);
        // 游标查询逻辑 - 只使用 createTime 作为游标
        if (lastCreateTime != null) {
            queryWrapper.lt("createTime", lastCreateTime);
        }
        // 排序
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            // 默认按创建时间降序排列
            queryWrapper.orderBy("createTime", false);
        }
        return queryWrapper;
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> exportAppChatHistoryMarkdown(Long appId, jakarta.servlet.http.HttpServletRequest request) {
        // 登录校验
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        // 权限校验：仅应用所有者可导出
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        if (!loginUser.getId().equals(app.getUserId())) {
            ThrowUtils.throwIf(true, ErrorCode.NO_AUTH_ERROR, "无权导出该应用的对话历史");
        }
        // 查询全部对话历史（按时间正序）
        QueryWrapper queryWrapper = com.mybatisflex.core.query.QueryWrapper.create()
                .eq("appId", appId)
                .orderBy("createTime", true);
        List<ChatHistory> chatHistoryList = this.list(queryWrapper);
        // 构建 Markdown
        String markdown = MarkdownUtils.buildMarkdownForExport(app, chatHistoryList);
        byte[] bytes = markdown.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        org.springframework.core.io.Resource resource = new ByteArrayResource(bytes);
        // 构建文件名
        String fileName = MarkdownUtils.sanitizeFileName("app-" + appId + "-chat-history-" +
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(java.time.LocalDateTime.now()) + ".md");
        // 返回文件下载响应
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .header("Content-Type", "text/markdown; charset=UTF-8")
                .header("Cache-Control", "no-store")
                .body(resource);
    }

    @Override
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> exportChatHistoryMarkdownAdmin(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 查询全部对话历史（按时间正序）
        com.mybatisflex.core.query.QueryWrapper queryWrapper = com.mybatisflex.core.query.QueryWrapper.create()
                .eq("appId", appId)
                .orderBy("createTime", true);
        List<ChatHistory> chatHistoryList = this.list(queryWrapper);
        // 构建 Markdown
        String markdown = MarkdownUtils.buildMarkdownForExport(app, chatHistoryList);
        byte[] bytes = markdown.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        org.springframework.core.io.Resource resource = new ByteArrayResource(bytes);
        String fileName =MarkdownUtils.sanitizeFileName("app-" + appId + "-chat-history-" +
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(java.time.LocalDateTime.now()) + ".md");
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .header("Content-Type", "text/markdown; charset=UTF-8")
                .header("Cache-Control", "no-store")
                .body(resource);
    }

}
