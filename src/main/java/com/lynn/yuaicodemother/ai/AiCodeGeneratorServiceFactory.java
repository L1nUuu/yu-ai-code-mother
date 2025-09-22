package com.lynn.yuaicodemother.ai;

/**
 * ClassName: AiCodeGeneratorServiceFactory
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/21 21:47
 * @Version 1.00
 */

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 服务创建工厂
 */
@Configuration
public class AiCodeGeneratorServiceFactory {
    @Resource
    private ChatModel chatModel;

    /**
     * 创建 AI 代码生成器服务
     * @return
     */
    @Bean
    public AiCodeGeneratorService createAiCodeGeneratorService() {
        return AiServices.create(AiCodeGeneratorService.class, chatModel);
    }
}
