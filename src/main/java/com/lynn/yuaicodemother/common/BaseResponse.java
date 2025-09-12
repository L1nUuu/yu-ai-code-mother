package com.lynn.yuaicodemother.common;

import com.lynn.yuaicodemother.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: BaseResponse
 * Description: 统一API响应结果封装
 *
 * @Author linz
 * @Creat 2025/9/12 15:00
 * @Version 1.00
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
