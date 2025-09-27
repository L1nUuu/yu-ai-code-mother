package com.lynn.yuaicodemother.service;

import com.lynn.yuaicodemother.model.dto.UserLoginRequest;
import com.lynn.yuaicodemother.model.dto.UserQueryRequest;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.model.vo.LoginUserVO;
import com.lynn.yuaicodemother.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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
     * 获取脱敏后的用户信息
     * @param user  用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户列表
     * @param userList 用户列表
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 用户注销
     */
    boolean userLogout(HttpServletRequest request);

    /**
     *  获取加密密码
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    //获得查询条件
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);
    
    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return 是否为管理员
     */
    boolean isAdmin(User user);
}