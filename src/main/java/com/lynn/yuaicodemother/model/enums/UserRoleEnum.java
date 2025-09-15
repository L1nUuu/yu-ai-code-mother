package com.lynn.yuaicodemother.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * ClassName: UserRoleEnum
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/14 17:26
 * @Version 1.00
 */
@Getter
public enum UserRoleEnum {
    USER("用户", "user"),
    ADMIN("管理员", "admin");

    private final String text;
    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;

    }

    /**
     * 根据value获取枚举
     */
    public static UserRoleEnum getByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum roleEnum : values()) {//values() 是 Java 枚举类型自带的静态方法，会返回该枚举类中所有枚举常量的数组。
            if (roleEnum.getValue().equals(value)) {
                return roleEnum;
            }
        }
        return null;
    }
}
