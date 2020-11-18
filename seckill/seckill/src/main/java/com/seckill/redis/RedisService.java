package com.seckill.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    /**
     * 获取对象
     * @param prefix
     * @param key
     * @param data
     * @param <T>
     * @return
     */
    public  <T> T get(KeyPrefix prefix,String key,Class<T> data){
        System.out.println("@RedisService-REDIES-GET!");
        Jedis jedis=null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key  className+":"+prefix;  BasePrefix:id1
            String realKey = prefix.getPrefix() + key;
            System.out.println("@RedisService-get-realKey:"+realKey);
            String sval = jedis.get(realKey);
            System.out.println("@RedisService-getvalue:"+sval);
            T t = stringToBean(sval, data);
            return  t;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除对象
     * @param prefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis=jedisPool.getResource();
            String realkey=prefix.getPrefix()+key;
            Long ret = jedis.del(realkey);
            return ret>0;//删除成功，返回大于0
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置单个，多个对象
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(BasePrefix prefix,String key,T value){
        System.out.println("RedisService-Redis-set");

        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            String realkey=prefix.getPrefix()+key;
            System.out.println("@RedisService-key:"+key);
            System.out.println("@RedisService-getPrefix:"+prefix.getPrefix());
            System.out.println("value: "+value);
            String s = beanToString(value);
            if(s==null||s.length()<=0){
                return false;
            }
            int seconds=prefix.expireSeconds();
            System.out.println("存活时间： "+seconds);
            if(seconds<0){//有效期：代表不过期，这样才去设置
                jedis.set(realkey,s);
            }else {
                //未设过期时间
                jedis.setex(realkey,seconds,s);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            String realkey=prefix.getPrefix()+key;
            return jedis.decr(realkey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr (KeyPrefix prefix,String key){
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }
    /**
     * 将字符串转换为Bean对象
     * parseInt()返回的是基本类型int 而valueOf()返回的是包装类Integer
     * Integer是可以使用对象方法的  而int类型就不能和Object类型进行互相转换 。
     * int a=Integer.parseInt(s);
     Integer b=Integer.valueOf(s);
     */
    public static <T> T stringToBean(String s,Class<T> clazz) {
        if(s==null||s.length()==0||clazz==null) {
            return null;
        }
        if(clazz==int.class||clazz==Integer.class) {
            return ((T) Integer.valueOf(s));
        }else if(clazz==String.class) {
            return (T) s;
        }else if(clazz==long.class||clazz==Long.class) {
            return (T) Long.valueOf(s);
        }else {
            JSONObject json= JSON.parseObject(s);
            return JSON.toJavaObject(json, clazz);
        }
    }
    public static <T> String beanToString(T value){
        if (value==null)
            return null;
        Class<?> clazz=value.getClass();
        if(clazz==int.class||clazz==Integer.class){
            return ""+value;
        }else if(clazz==String.class){
            return ""+value;
        }else if (clazz==long.class||clazz==Long.class){
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }
    private void returnToPool(Jedis jedis) {
        if(jedis!=null) {
            jedis.close();
        }
    }
    public <T> boolean set(String key,T value){
        Jedis jedis=null;
        try{
           jedis= jedisPool.getResource();
            String s = beanToString(value);
            if(s==null){
                return  false;
            }
            jedis.set(key,s);
            return true;
        }finally {
            returnToPool(jedis);
        }
    }
    public <T> T get(String key,Class<T> data){
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            System.out.println("jedis"+jedis);
            String sval=jedis.get(key);
            System.out.println("sval"+sval);
            //string转为Bean后传出
            T t = stringToBean(sval, data);
            return t;
        }
        finally {
            returnToPool(jedis);
        }
    }
}
