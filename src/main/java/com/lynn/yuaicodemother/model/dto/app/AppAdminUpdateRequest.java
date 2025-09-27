package com.lynn.yuaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: AppAdminUpdateRequest
 * Description: 管理员更新应用请求
 *
 * @Author linz
 * @Create 2025/9/27 20:47
 * @Version 1.00
 */
@Data
public class AppAdminUpdateRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * id
     */
    private Long id;
    
    /**
     * 应用名称
     */
    private String appName;
    
    /**
     * 应用封面
     */
    private String cover;
    
    /**
     * 优先级
     */
    private Integer priority;
}