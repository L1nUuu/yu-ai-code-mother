package com.lynn.yuaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: AppAddRequest
 * Description: 应用创建请求
 *
 * @Author linz
 * @Create 2025/9/27 20:45
 * @Version 1.00
 */
@Data
public class AppAddRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

}