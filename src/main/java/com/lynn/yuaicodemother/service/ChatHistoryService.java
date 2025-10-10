package com.lynn.yuaicodemother.service;

import com.lynn.yuaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.lynn.yuaicodemother.model.entity.ChatHistory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author Linz
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    /**
     * 添加对话消息
     * @param appId 应用 Id
     * @param message 消息
     * @param messageType 消息类型
     * @param userId 用户 Id
     * @return 是否添加成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);


    /**
     * 根据应用 Id 删除对话历史
     * @param appId 应用 Id
     * @return 是否删除成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 分页查询应用下的对话历史
     * @param appId 应用 Id
     * @param pageSize 页大小
     * @param lastCreateTime 最后创建时间
     * @param loginUser 登录用户
     * @return 对话历史
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    /**
     * 加载应用下的对话历史到内存
     * @param appId 应用 Id
     * @param chatMemory 对话内存
     * @param maxCount 最多加载多少条
     * @return 加载成功的记录数
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);

    /**
     * 构造查询条件
     * @param chatHistoryQueryRequest 对话查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);


    /**
     * 导出应用下的对话历史为 Markdown 文件
     * @param appId 应用 Id
     * @param request 请求
     * @return 导出的 Markdown 文件
     */
    ResponseEntity<Resource> exportAppChatHistoryMarkdown(Long appId, jakarta.servlet.http.HttpServletRequest request);

    /**
     * 导出应用下的对话历史为 Markdown 文件
     * @param appId 应用 Id
     * @return 导出的 Markdown 文件
     */
    ResponseEntity<org.springframework.core.io.Resource> exportChatHistoryMarkdownAdmin(Long appId);

}
