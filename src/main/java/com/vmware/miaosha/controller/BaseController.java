package com.vmware.miaosha.controller;

import com.vmware.miaosha.error.BusinessException;
import com.vmware.miaosha.error.EnumBusinessError;
import com.vmware.miaosha.response.CommonReturnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

public class BaseController {

    final static Logger logger = LoggerFactory.getLogger(BaseController.class);

    // 定义ExceptionHandler解决未被Controller层吸收的exception
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonReturnType exceptionHandler(Exception ex){
        Map<String, Object> responseData = new HashMap<>();
        if(ex instanceof BusinessException){
            BusinessException businessException = (BusinessException)ex;
            responseData.put("errorCode", businessException.getErrorCode());
            responseData.put("errorMsg", businessException.getErrorMsg());
        } else {
            responseData.put("errorCode", EnumBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errorMsg", EnumBusinessError.UNKNOWN_ERROR.getErrorMsg());
        }

        return CommonReturnType.create(responseData, "failed");
    }
}
