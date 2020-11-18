package com.seckill.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码格式
 */
public class ValidatorUtil {
    private static  final Pattern mobile_pattern = Pattern.compile("1\\d{10}");//正则表达式代表1-10数字

    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)){
            return false;
        }
        Matcher m = mobile_pattern.matcher(src);
        return m.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("11211111111"));
    }
}
