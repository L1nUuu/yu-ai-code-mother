package com.lynn.yuaicodemother.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName: AuthCheck
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/16 13:59
 * @Version 1.00
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    /**
     * 必须有该角色
     *
     * @return
     */
    String mustRole() default "";
}
