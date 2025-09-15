package com.lynn.yuaicodemother.service;

import com.mybatisflex.core.service.IService;
import com.lynn.yuaicodemother.model.entity.User;

/**
 * 用户 服务层。
 *
 * @author Linz
 */
public interface UserService extends IService<User> {

    long userRegister(String userAccount,String userPassword,String checkPassword);
}
