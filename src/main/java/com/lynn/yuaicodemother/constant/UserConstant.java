package com.lynn.yuaicodemother.constant;

/**
 * ClassName: UserConstant
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/16 11:11
 * @Version 1.00
 */

/**
 * 用户常量 定义为一个接口 会默认加final
 */
public interface UserConstant {
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    // region 权限
    //默认角色
    String DEFAULT_ROLE = "user";
    //管理员角色
    String ADMIN_ROLE = "admin";

    // endregion
}
