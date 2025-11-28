package com.lynn.yuaicodemother.ai;

/**
 * ClassName: AiCodeGeneratorServiceFactory
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/21 21:47
 * @Version 1.00
 */

import cn.hutool.ai.core.Message;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.lynn.yuaicodemother.ai.tools.*;
import com.lynn.yuaicodemother.config.ReasoningStreamingChatModelConfig;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.guardrail.PromptSafetyInputGuardrail;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import com.lynn.yuaicodemother.service.ChatHistoryOriginalService;
import com.lynn.yuaicodemother.service.ChatHistoryService;
import com.lynn.yuaicodemother.util.SpringContextUtil;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * AI 服务创建工厂
 */
@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {

    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;
    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private ChatHistoryOriginalService chatHistoryOriginalService;


    /**
     * AI 实例缓存
     * 缓存策略：
     * - 最大缓存1000个实例
     * - 写入后30分钟过期
     * - 访问后10分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) ->
                    log.debug("AI服务实例被移除，缓存键: {}，原因为：{}", key, cause))
            .build();

    /**
     * 根据应用ID获取 AI 服务实例（为了兼容老逻辑）
     * -- 1.如果缓存中存在，则直接返回
     * -- 2.如果缓存中不存在，则创建并缓存
     *
     * @param appId
     * @return
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.MULTI_FILE);
    }

    /**
     * 获取 AI 服务实例
     * -- 1.如果缓存中存在，则直接返回
     * -- 2.如果缓存中不存在，则创建并缓存
     *
     * @param appId
     * @return
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
    }

    /**
     * 创建 AI 服务实例
     *
     * @param appId       应用ID
     * @param codeGenType 代码生成类型
     * @return
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(Long appId, CodeGenTypeEnum codeGenType) {
        log.info("为appId: {}，创建新的 AI 服务实例", appId);
        AiCodeGeneratorService aiCodeGeneratorService;
        // 根据appId 创建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(60)  // 一次工具调用也算一次记忆
                .build();


        // 根据代码生成类型选择不同的模型配置
        switch (codeGenType) {

            // Vue 项目生成，使用工具调用和推理模型
            case VUE_PROJECT -> {
                // 使用多例模式下的streamingChatModel解决并发问题
                StreamingChatModel reasoningStreamingChatModel = SpringContextUtil.getBean("reasoningStreamingChatModelPrototype", StreamingChatModel.class);

                // 数据库加载对话历史到缓存中，由于多了工具调用相关信息，加载的数量稍微多一点
                chatHistoryOriginalService.loadOriginalChatHistoryToMemory(appId, chatMemory, 50);
                aiCodeGeneratorService = AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(reasoningStreamingChatModel)
                        .chatMemoryProvider(
                                memoryId -> chatMemory
                        )
                        .inputGuardrails(new PromptSafetyInputGuardrail()) // 添加输入护轨
                        .tools(
                                new FileWriteTool(),
                                new FileReadTool(),
                                new FileDeleteTool(),
                                new FileDirReadTool(),
                                new FileModifyTool())
                        .hallucinatedToolNameStrategy(toolExecutionRequest ->
                                ToolExecutionResultMessage.from(
                                        toolExecutionRequest,
                                        "Error：there is no tool called " + toolExecutionRequest.name()
                                )) //这个策略，当模型调用的tool不存在时，返回一个错误消息
                        .build();

            }


            // HTML 和 多文件 项目生成，使用流式对话模型
            case HTML, MULTI_FILE -> {
                // 使用多例模式下的streamingChatModel解决并发问题
                StreamingChatModel openAiStreamingChatModel = SpringContextUtil.getBean("streamingChatModelProtoType", StreamingChatModel.class);

                chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
                aiCodeGeneratorService = AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(openAiStreamingChatModel)
                        .inputGuardrails(new PromptSafetyInputGuardrail()) // 添加输入护轨
                        .chatMemory(chatMemory)
                        .build();
            }
            default -> {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型：" + codeGenType.getValue());
            }
        }
        return aiCodeGeneratorService;
    }


    /**
     * 创建 AI 代码生成器服务
     *
     * @return
     */
    @Bean
    public AiCodeGeneratorService createAiCodeGeneratorService() {
        return getAiCodeGeneratorService(0L);
    }

    /**
     * 构造缓存键
     *
     * @param appId       应用ID
     * @param codeGenType 代码生成类型
     * @return 缓存键
     */
    private String buildCacheKey(Long appId, CodeGenTypeEnum codeGenType) {
        return appId + "_" + codeGenType.getValue();
    }
}
