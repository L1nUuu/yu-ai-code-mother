package com.lynn.yuaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lynn.yuaicodemother.constant.UserConstant;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.dto.UserLoginRequest;
import com.lynn.yuaicodemother.model.dto.UserQueryRequest;
import com.lynn.yuaicodemother.model.enums.UserRoleEnum;
import com.lynn.yuaicodemother.model.vo.LoginUserVO;
import com.lynn.yuaicodemother.model.vo.UserVO;
import com.lynn.yuaicodemother.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lynn.yuaicodemother.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author Linz
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 新用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }

        //2.查看用户名是否存在
        long count = this.mapper.selectCountByQuery(new QueryWrapper().eq("userAccount", userAccount));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        //3.密码加密
        String encryptPassword = getEncryptPassword(userPassword);//获得加密后的密码

        //4.创建用户
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("用户" + IdUtil.simpleUUID().substring(0, 8));
        user.setUserAvatar("https://i0.hdslb.com/bfs/openplatform/fdfe59f72b5fc8f8d449ec2d05dd1590058a7aef.jpg");
        user.setUserRole(UserRoleEnum.USER.getValue());
        user.setUserProfile("这个人很懒，什么都没有留下~");//默认简介

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }

        return user.getId();
    }

    /**
     * 获取脱敏后的登录用户信息
     *
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验参数
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        //2.加密
        String encryptPassword = getEncryptPassword(userPassword);
        //3.查询用户是否存在
        User user = this.mapper.selectOneByQuery(new QueryWrapper()
                .eq("userAccount", userAccount)
                .eq("userPassword", encryptPassword));
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        //4.如果用户存在，记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        //5.返回脱敏后的用户信息
        return this.getLoginUserVO(user);
    }

    //获取当前登录用户信息
    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //从数据库查询当前用户信息
        currentUser = this.mapper.selectOneById(currentUser.getId());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollectionUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
/*
        List<UserVO> userVOList = new ArrayList<>();
        for(User user : userList){
            userVOList.add(this.getUserVO(user));
        }
        return userVOList;*/
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    //注销登录
    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        //1.先判空
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        //2.获取查询条件
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id",id)
                .eq("userRole",userRole)
                .like("userAccount",userAccount)
                .like("userName",userName)
                .like("userProfile",userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));


    }

    /**
     * 密码加密
     *
     * @param userPassword
     * @return
     */
    public String getEncryptPassword(String userPassword) {
        //盐值，混淆密码
        final String SALT = "lynn";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
    }
}
