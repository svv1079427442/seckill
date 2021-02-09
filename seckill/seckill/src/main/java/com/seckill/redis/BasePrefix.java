package com.seckill.redis;

public abstract class BasePrefix implements KeyPrefix{
    private int expireSeconds;
    private String prefix;
    public BasePrefix(){
    }
    public BasePrefix(String prefix) {
        //this(0, prefix);//默认使用0，不会过期
        this.expireSeconds=0;
        this.prefix=prefix;
    }
    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }
    public int expireSeconds(){
        return expireSeconds;//缓存有效时间

    }
    //前缀为类名:+prefix
    public String getPrefix(){
        String classname = getClass().getSimpleName();
        return classname+":"+prefix;
    }
}
