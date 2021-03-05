package com.seckill.redis;

public class AccessKey extends BasePrefix{


    public AccessKey(int expireSeconds, String prefix){
        super(expireSeconds,prefix);
    }
    public static AccessKey access = new AccessKey(5,"access");
    //动态设置有效期
    public static AccessKey expire(int expireSeconds) {
        return new AccessKey(expireSeconds,"access");
    }
}
