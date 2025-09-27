package com.lynn.yuaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: AppUpdateRequest
 * Description: 应用更新请求
 *
 * @Author linz
 * @Create 2025/9/27 20:46
 * @Version 1.00
 */
@Data
public class AppUpdateRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * id
     */
    private Long id;
    
    /**
     * 应用名称
     */
    private String appName;
}