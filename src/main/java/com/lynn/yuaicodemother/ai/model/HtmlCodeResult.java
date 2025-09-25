package com.lynn.yuaicodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * ClassName: HtmlCodeResult
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/21 22:22
 * @Version 1.00
 */

/**
 * Html 代码结果
 */
@Description("生成 Html 代码文件的结果")
@Data
public class HtmlCodeResult {
    /**
     * html 代码
     */
    @Description("Html 代码")
    private String htmlCode;

    /**
     * 描述
     */
    @Description("Html 代码的描述")
    private String description;
}
