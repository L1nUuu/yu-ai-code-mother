package com.lynn.yuaicodemother.ai;

import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

/**
 * ClassName: AiCodeGenTypeRoutingService
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/28 10:21
 * @Version 1.00
 */
public interface AiCodeGenTypeRoutingService {

    /**
     * 根据用户需求智能选择代码生成类型
     * @param userPrompt 用户输入的需求描述
     * @return 代码生成类型枚举
     */
    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(String userPrompt);
}
