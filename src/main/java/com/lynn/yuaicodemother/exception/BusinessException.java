package com.lynn.yuaicodemother.exception;

import lombok.Getter;

/**
 * ClassName: BusinessException
 * Description: 自定义业务异常，不建议直接抛出Java内置的RuntimeException
 *
 * @Author linz
 * @Creat 2025/9/12 14:57
 * @Version 1.00
 */
@Getter
public class BusinessException extends RuntimeException{

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code,String message){
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorcode, String message){
        super(message);
        this.code = errorcode.getCode();
    }

}
