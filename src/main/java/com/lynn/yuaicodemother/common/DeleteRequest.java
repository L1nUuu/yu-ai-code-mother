package com.lynn.yuaicodemother.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: DeleteRequest
 * Description: 删除请求封装类
 *
 * @Author linz
 * @Creat 2025/9/12 15:03
 * @Version 1.00
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
