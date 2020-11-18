package com.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static  String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    //固定salt，和密码做拼接
    private static final String salt="1q2w3e4r";
    public static String inputPassToFormPass(String inputPass){
        String str=""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }
    //二次MD5
    public static String formPassToDBPass(String formPass,String salt){
        String str=""+salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }
    public static String inputPassToDBPass(String input,String saltDB){
        String formPass = inputPassToFormPass(input);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }
    public static void main(String[] args) {
       System.out.println(inputPassToFormPass("123456"));
        System.out.println("1："+formPassToDBPass("78411406ec8925f1e07555989db84e2d",salt));
       System.out.println("2：" + inputPassToDBPass("123456", salt));
       //System.out.println("2："+inputPassToDBPass("123456",salt));

    }
}
