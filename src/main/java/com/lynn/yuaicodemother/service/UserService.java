package com.lynn.yuaicodemother.service;

import com.lynn.yuaicodemother.model.dto.UserLoginRequest;
import com.lynn.yuaicodemother.model.vo.LoginUserVO;
import com.mybatisflex.core.service.IService;
import com.lynn.yuaicodemother.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户 服务层。
 *
 * @author Linz
 */
public interface UserService extends IService<User> {

    // 用户注册
    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 获取已脱敏的登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户登录
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户信息(未脱敏)
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     */
    boolean userLogout(HttpServletRequest request);
}
