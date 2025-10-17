package com.lynn.yuaicodemother.core.handler;

import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.lynn.yuaicodemother.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * ClassName: SimpleTextStreamHandler
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/16 16:43
 * @Version 1.00
 */
@Slf4j
public class SimpleTextStreamHandler {

    /**
     * 处理传统流（HTML、MULTI_FILE）
     * 直接收集完整的文本响应
     * @param originalFlux 原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId 应用ID
     * @param loginUser 登录用户
     * @return  处理后的流
     */
    public Flux<String> handle(Flux<String> originalFlux, ChatHistoryService chatHistoryService,Long appId, User loginUser){
        StringBuilder aiResponseBuilder = new StringBuilder();
        return originalFlux.map(chunk -> {
                    // 实时收集代码片段
                    aiResponseBuilder.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> {
                    // 流式返回完成后，保存AI消息到对话历史中
                    String aiResponseBuilderString = aiResponseBuilder.toString();
                    chatHistoryService.addChatMessage(appId, aiResponseBuilderString, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
                })
                .doOnError(error -> {
                    // 如果AI回复失败，也需要保存记录到数据库中
                    String errorMessage = "AI回复失败：" +  error.getMessage();
                    chatHistoryService.addChatMessage(appId, errorMessage, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
                });
    }
}
