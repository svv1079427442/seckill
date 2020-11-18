package com.seckill.util;

import java.util.UUID;

public class UUIDUtil {
    public static String uuid(){
        //token都是随机生成
        return UUID.randomUUID().toString().replace("-","");//去掉原生的"-"
    }
}
