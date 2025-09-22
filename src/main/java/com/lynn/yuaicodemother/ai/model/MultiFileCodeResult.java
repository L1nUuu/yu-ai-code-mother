package com.lynn.yuaicodemother.ai.model;

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
public class MultiFileCodeResult {
    /**
     * html代码
     */
    private String htmlCode;

    /**
     * css代码
     */
    private String cssCode;

    /**
     * js代码
     */
    private String jsCode;

    /**
     * 描述
     */
    private String description;
}
