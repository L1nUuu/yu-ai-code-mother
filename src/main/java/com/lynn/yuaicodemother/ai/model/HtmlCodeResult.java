package com.lynn.yuaicodemother.ai.model;

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
@Data
public class HtmlCodeResult {
    /**
     * html 代码
     */
    private String htmlCode;

    /**
     * 描述
     */
    private String description;
}
