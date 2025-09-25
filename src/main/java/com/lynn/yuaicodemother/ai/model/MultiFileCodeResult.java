package com.lynn.yuaicodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * ClassName: MultiFileCodeResult
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/21 22:23
 * @Version 1.00
 */
@Data
@Description("生成多个代码文件的结果")
public class MultiFileCodeResult {
    /**
     * html代码
     */
    @Description("Html 代码")
    private String htmlCode;

    /**
     * css代码
     */
    @Description("CSS 代码")
    private String cssCode;

    /**
     * js代码
     */
    @Description("JS 代码")
    private String jsCode;
    /**
     * 描述
     */
    @Description("生成代码的描述")
    private String description;
}
