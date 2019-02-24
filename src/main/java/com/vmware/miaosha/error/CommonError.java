package com.vmware.miaosha.error;

public interface CommonError {

    // 获取错误码
    int getErrorCode();

    // 获取错误消息
    String getErrorMsg();

    // 设置错误消息，用于覆盖Enumeration中设置的默认错误消息
    CommonError setErrorMsg(String errorMsg);
}
