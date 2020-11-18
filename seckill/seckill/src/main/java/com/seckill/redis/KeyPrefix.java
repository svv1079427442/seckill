package com.seckill.redis;

public interface KeyPrefix {
    //有效期
    public int expireSeconds();
    //前缀
    public String getPrefix();
}
