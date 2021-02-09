package com.seckill.redis;

public class OrderKey extends BasePrefix{
    public OrderKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }
    public static OrderKey getSeckillOrderByUidAndGid=new OrderKey(600,"ms_uidgid");
}
