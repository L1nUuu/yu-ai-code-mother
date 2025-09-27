package com.lynn.yuaicodemother.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UserAddRequest
 * Description: 用户创建请求
 *
 * @Author linz
 * @Creat 2025/9/18 14:16
 * @Version 1.00
 */
@Data
public class UserAddRequest implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;
}
