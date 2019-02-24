package com.vmware.miaosha;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TestB extends SuperTest {
    private String nameB;

    @Override
    public void getName() {
        System.out.println("子类" + nameB);
//        super.getName();
    }

    public TestB(String name) {
        super();
        this.nameB = name;
    }

    private static String encodeByMd5(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder base64Encoder = Base64.getEncoder();
        // 加密字符串
        return base64Encoder.encodeToString(md5.digest(string.getBytes("utf-8")));
    }

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        System.out.println(encodeByMd5("123456"));


    }
}
