package com.lynn.yuaicodemother.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: AiCodeGenAppNameServiceFactory
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/28 11:29
 * @Version 1.00
 */
@Slf4j
@Configuration
public class AiCodeGenAppNameServiceFactory {
    @Resource
    private ChatModel chatModel;


    /**
     * 创建AI代码生成应用名称服务实例
     */
    @Bean
    public AiCodeGenAppNameService aiCodeGenAppNameService() {
        return AiServices.builder(AiCodeGenAppNameService.class)
                .chatModel(chatModel)
                .build();
    }
}
