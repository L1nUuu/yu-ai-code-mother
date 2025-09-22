package com.lynn.yuaicodemother.ai;

import com.lynn.yuaicodemother.ai.model.HtmlCodeResult;
import com.lynn.yuaicodemother.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;

/**
 * ClassName: AiCodeGeneratorService
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/21 21:32
 * @Version 1.00
 */
public interface AiCodeGeneratorService {
    /**
     * 生成 HTML 代码
     * @param userMessage 用户提示词
     * @return AI的输出结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 生成 多文件 代码
     * @param userMessage 用户提示词
     * @return AI的输出结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(String userMessage);
}
