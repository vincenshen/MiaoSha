package com.vmware.miaosha.response;

/**
 * 通用返回结果类
 * 包含请求结果 status : "success" or "failed"
 * 包含请求数据 data : {}
 */
public class CommonReturnType {

    // 表明对应请求的返回处理结果为: "success" or "failed"
    private String status;

    // 若status=success, 则data内返回前端需要的json数据
    // 若status=failed, 则data内使用通用的错误码格式
    private Object data;

    // 通用静态工厂方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result, "success");
    }

    // 封装返回数据data
    public static CommonReturnType create(Object result, String status){
        CommonReturnType returnType = new CommonReturnType();
        returnType.setStatus(status);
        returnType.setData(result);
        return returnType;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
