package com.vmware.miaosha.error;

/**
 * 继承至CommonError
 */
public enum EnumBusinessError implements CommonError {

    // 10000开头为通用错误类型
    UNKNOWN_ERROR(10001, "未知错误"),
    PARAMETER_VALIDATION_ERROR(10002, "参数不合法"),

    // 20000开头为用户信息相关错误码定义
    USER_NOT_EXIST(20001, "用户不存在"),
    PHONE_NOT_EXIST(20002, "手机号不存在"),
    USER_LOGIN_FAIL(20003, "手机号或密码错误"),
    USER_NOT_LOGIN(20004, "用户未登录"),

    // 30000开头为商品相关错误码定义
    STOCK_NOT_ENOUGH(30001, "库存不足"),
    ;

    EnumBusinessError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    private int errorCode;
    private String errorMsg;

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    // 主要的作用是可以修改 某个错误码对应的 errorMessage
    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
}
