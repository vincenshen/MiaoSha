package com.vmware.miaosha.service;

import com.vmware.miaosha.error.BusinessException;
import com.vmware.miaosha.service.model.UserModel;


public interface UserService {

    // 通过用户ID获取用户对象的方法
    UserModel getUserById(Integer id);

    // 用户注册方法
    void register(UserModel userModel) throws BusinessException;

    // 用户认证
    // telPhone是用户的注册手机
    // encryptPassword是加密后的密码
    UserModel validateLogin(String telPhone, String encryptPassword) throws BusinessException;

}
