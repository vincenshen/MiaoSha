package com.vmware.miaosha.controller;

import com.vmware.miaosha.controller.viewobject.UserVO;
import com.vmware.miaosha.error.BusinessException;
import com.vmware.miaosha.error.EnumBusinessError;
import com.vmware.miaosha.response.CommonReturnType;
import com.vmware.miaosha.service.UserService;
import com.vmware.miaosha.service.model.UserModel;
import com.vmware.miaosha.utils.PasswordEncrypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


@Controller(value = "user")
@RequestMapping(value = "/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    // 当前用户的HttpRequest
    @Autowired
    private HttpServletRequest httpServletRequest;

    // 用户登录接口
    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telPhone") String telPhone,
                                  @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 第一步，入参校验
        if(StringUtils.isEmpty(telPhone) || StringUtils.isEmpty(password)){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "手机号和密码不能为空");
        }

        // 第二步，校验用户登录是否合法
        UserModel userModel = userService.validateLogin(telPhone, PasswordEncrypt.encodeByMd5(password));

        // 第三步，加入到用户登录后的Session中
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);
    }

    // 用户注册接口
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telPhone") String telPhone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Byte gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 验证手机号码和OTPCode相符合
        String inSessionOtpCode = (String)httpServletRequest.getSession().getAttribute(telPhone);
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOtpCode)){
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }

        // 用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(age);
        userModel.setGender(gender);
        userModel.setTelphone(telPhone);
        userModel.setRegisterMode("byPhone");
        userModel.setEncrptPassword(PasswordEncrypt.encodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }


    // 用户获取OTP短信接口
    @RequestMapping(value = "/otp", method = {RequestMethod.POST}, consumes = {"application/x-www-form-urlencoded"})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telPhone") String telPhone){
        // 按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        if(randomInt < 10000){
            randomInt += 10000;
        }
        String otpCode = String.valueOf(randomInt);

        // 将OTP验证码同对应用户的手机号关联
        // 如果是分布式处理，通常是将手机号码和OtpCode写到Redis中, 因为Redis天生就是Key-Value形式存在，并且还有有效期。
        // 这里使用HTTPSession来绑定手机号和OtpCode
        httpServletRequest.getSession().setAttribute(telPhone, otpCode);

        // 将OTPCode发送给用户（通常是短信）
        logger.warn("telPhone = " + telPhone + "&OTPCode = " + otpCode);
        return  CommonReturnType.create(null);
    }

    @GetMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        // 调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        // 获取对应的用户不存在
        if(userModel == null){
            throw new BusinessException(EnumBusinessError.USER_NOT_EXIST);
        }

        // 将核心领域模型用户对象转换为可供UI使用的ViewObject
        // 而不是直接将UserModel返回给前端
        UserVO userVO = convertFromModel(userModel);

        // 返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }

}
