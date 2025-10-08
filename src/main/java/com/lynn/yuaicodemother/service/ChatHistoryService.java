package com.lynn.yuaicodemother.service;

import com.lynn.yuaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.lynn.yuaicodemother.model.entity.ChatHistory;

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
     * 构造查询条件
     * @param chatHistoryQueryRequest 对话查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);



}
