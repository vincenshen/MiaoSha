package com.vmware.miaosha.service.impl;

import com.vmware.miaosha.dao.UserDOMapper;
import com.vmware.miaosha.dao.UserPasswordDOMapper;
import com.vmware.miaosha.dataobject.UserDO;
import com.vmware.miaosha.dataobject.UserPasswordDO;
import com.vmware.miaosha.error.BusinessException;
import com.vmware.miaosha.error.EnumBusinessError;
import com.vmware.miaosha.service.UserService;
import com.vmware.miaosha.service.model.UserModel;
import com.vmware.miaosha.validator.ValidationResult;
import com.vmware.miaosha.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService {

    private final UserDOMapper userDOMapper;

    private final UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    public UserServiceImpl(UserDOMapper userDOMapper, UserPasswordDOMapper userPasswordDOMapper) {
        this.userDOMapper = userDOMapper;
        this.userPasswordDOMapper = userPasswordDOMapper;
    }

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        // 调用UserDOMapper获取到对应的用户dataObject
        // 在企业级的应用开发中，不能直接将UserDO直接透传给用户端，一个Model对象可以是多个表的映射
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if(userDO == null){
            return null;
        }

        // 通过用户ID获取对应的用户加密密码
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        return convertFromDataObject(userDO, userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel == null){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        ValidationResult validationResult = validator.validate(userModel);
        if(validationResult.isHasErrors()){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, validationResult.getErrorMsg());
        }

        // 实现model -> dataObject方法
        UserDO userDO = convertFromModel(userModel);

        // insertSelective与insert的区别：
        // insertSelective 如果数据库中表字段设置了默认值，那么插入的值为空，就使用数据库默认的值。
        // insert 如果插入的值为空，就会使用null覆盖数据库中表字段设置的默认值
        try{
            userDOMapper.insertSelective(userDO);
        }catch (DuplicateKeyException ex){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "该手机号已被注册");
        }

        userModel.setId(userDO.getId());
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

    }

    @Override
    public UserModel validateLogin(String telPhone, String encryptPassword) throws BusinessException {
        // 用过telPhone获取用户信息
        UserDO userDO = userDOMapper.selectByTelPhone(telPhone);
        if(userDO == null){
            throw new BusinessException(EnumBusinessError.PHONE_NOT_EXIST);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);
        // 比对用户信息中加密的密码是否一致
        if(!StringUtils.equals(encryptPassword, userModel.getEncrptPassword())){
            throw new BusinessException(EnumBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);

        if(userPasswordDO != null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }
}
