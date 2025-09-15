package com.lynn.yuaicodemother.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.enums.UserRoleEnum;
import com.lynn.yuaicodemother.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 用户 服务层实现。
 *
 * @author Linz
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 新用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验参数
        if (StrUtil.hasBlank(userAccount,userPassword,checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度过短");
        }
        if(userPassword.length()<8||checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度过短");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }

        //2.查看用户名是否存在
        long count = this.mapper.selectCountByQuery(new QueryWrapper().eq("userAccount", userAccount));
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
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
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败，数据库错误");
        }

        return user.getId();


    }

    /**
     * 密码加密
     * @param userPassword
     * @return
     */
    public String getEncryptPassword(String userPassword){
        //盐值，混淆密码
        final String SALT = "lynn";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
    }
}
