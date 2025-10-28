package com.lynn.yuaicodemother.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * ClassName: AiCodeGenAppNameService
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/28 11:21
 * @Version 1.00
 */
public interface AiCodeGenAppNameService {

    @SystemMessage(fromResource = "prompt/app-name-generator-prompt.txt")
    String getAppName(String userPrompt);
}
