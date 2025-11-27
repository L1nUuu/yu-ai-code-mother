package com.lynn.yuaicodemother.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.Duration;

/**
 * ClassName: NamingChatModelConfig
 * Description: 专门用于生成名称的 ChatModel 配置 (多例)
 *
 * @Author linz
 * @Version 1.00
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.naming-chat-model")
@Data
public class NamingChatModelConfig {

    private String baseUrl;

    private String apiKey;

    private String modelName;

    /**
     * 针对起名场景，建议设置较小的值（例如 50-100），防止模型输出无关解释
     */
    private Integer maxTokens;

    /**
     * 针对起名场景，建议 0.7-1.0 以获得更多创意
     */
    private Double temperature;
    
    /**
     * 超时时间，默认 60秒
     */
    private Long timeoutSeconds = 60L;

    private Boolean logRequests = false;

    private Boolean logResponses = false;

    @Bean
    @Scope("prototype")
    public ChatModel namingChatModelPrototype() {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .maxTokens(maxTokens) // 限制生成长度，适合起名
                .temperature(temperature) // 控制创意程度
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .logRequests(logRequests)
                .logResponses(logResponses)
                .build();
    }
}