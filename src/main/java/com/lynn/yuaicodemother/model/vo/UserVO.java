package com.lynn.yuaicodemother.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: UserVO
 * Description: 脱敏之后的用户信息（更少信息，用于展示给其他用户）
 *            相比LoginUserVO，少了更新时间
 *
 * @Author linz
 * @Creat 2025/9/18 14:27
 * @Version 1.00
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
