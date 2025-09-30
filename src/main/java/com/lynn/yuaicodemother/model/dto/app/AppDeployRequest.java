package com.lynn.yuaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: AppDeployRequest
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/30 09:26
 * @Version 1.00
 */
@Data
public class AppDeployRequest implements Serializable {
    /**
     * 应用id
     */
    private Long appId;

    private static final long serialVersionUID = 1L;
}
