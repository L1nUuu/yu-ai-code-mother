package com.lynn.yuaicodemother.exception;

import com.lynn.yuaicodemother.common.BaseResponse;
import com.lynn.yuaicodemother.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ClassName: GlobalExceptionHandler
 * Description: 全局异常处理器
 *
 * @Author linz
 * @Creat 2025/9/12 15:10
 * @Version 1.00
 */
@Hidden // 该注解用于隐藏在Swagger文档中 不在API文档中显示
@RestControllerAdvice // 该注解会拦截所有加了@RestController注解的类的异常
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
