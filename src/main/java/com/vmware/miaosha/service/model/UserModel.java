package com.vmware.miaosha.service.model;

import javax.validation.constraints.*;

/**
 * 1  @NotEmpty :不能为null，且Size>0
 * 2  @NotNull:不能为null，但可以为empty,没有Size的约束
 * 3  @NotBlank:只用于String,不能为null且trim()之后size>0
 */

public class UserModel {

    private Integer id;

    @NotBlank(message = "name不能为空")
    private String name;

    @NotNull(message = "性别不能为空")
    private Byte gender;

    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄必须小于0")
    @Max(value = 150, message = "年龄不能大于150")
    private Integer age;

    @NotNull(message = "手机号码不能为空")
    @Size(min = 11, max = 11, message = "手机号码必须为11位")
    private String telphone;

    @NotNull(message = "注册方式不能为空")
    private String registerMode;
    private String thirdPartyId;

    @NotNull(message = "密码不能为空")
    private String encrptPassword;

    public String getEncrptPassword() {
        return encrptPassword;
    }

    public void setEncrptPassword(String encrptPassword) {
        this.encrptPassword = encrptPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getRegisterMode() {
        return registerMode;
    }

    public void setRegisterMode(String registerMode) {
        this.registerMode = registerMode;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }
}
