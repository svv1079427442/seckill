package com.seckill.redis;

public class SeckillKey extends BasePrefix{
    public SeckillKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }
    public static SeckillKey isGoodsOver=new SeckillKey(0,"go");
    //有效期60s
    public static SeckillKey getMiaoshaPath=new SeckillKey(60,"mp");
    //验证码   300s有效期
    public static SeckillKey getMiaoshaVertifyCode=new SeckillKey(300,"vc");
}
