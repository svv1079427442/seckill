package com.seckill.service;

import com.seckill.dao.SeckillUserDao;
import com.seckill.dao.UserDao;
import com.seckill.exception.GlobalException;
import com.seckill.pojo.SeckillUser;
import com.seckill.redis.RedisService;
import com.seckill.redis.SeckillUserKey;
import com.seckill.result.CodeMsg;
import com.seckill.util.MD5Util;
import com.seckill.util.UUIDUtil;
import com.seckill.vo.LoginVo;
import com.seckill.vo.RegisterVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
@Service
public class SeckillUserService {
    public static final String COOKIE1_NAME_TOKEN="token";
    @Autowired
    SeckillUserDao seckillUserDao;
    @Autowired
    RedisService redisService;
    @Autowired
    UserDao userDao;
    public SeckillUser getById(long id){
        SeckillUser user = redisService.get(SeckillUserKey.getById, "" + id, SeckillUser.class);
       //先去缓存查数据
        Date systemDate = new Date();
        if(user!=null ){
            return user;
        }
        //没有就到数据库查
        user = seckillUserDao.getById(id);
        //设置缓存
        if(user!=null){
            redisService.set(SeckillUserKey.getById,""+id,user);
        }
        return user;
    }
    //更新密码
    public boolean updatePassword(String token,long id,String formPass){
        SeckillUser user = getById(id);
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOTEXIST);
        }
        //更新数据库
        SeckillUser update=new SeckillUser();
        update.setId(id);
        update.setPwd(MD5Util.formPassToDBPass(formPass,user.getSalt()));
        seckillUserDao.update(update);
        //修改缓存
        redisService.delete(SeckillUserKey.getById,""+id);
        user.setPwd(update.getPwd());
        redisService.set(SeckillUserKey.token,token,user);
        return true;
    }
    public boolean login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        //更新登录次数，最后一次登陆时间
        SeckillUser users = seckillUserDao.getById(Long.parseLong(loginVo.getMobile()));
        users.setLoginCount(users.getLoginCount()+1);
        users.setLastLoginDate(new Date());
        users.setId(Long.parseLong(loginVo.getMobile()));
        seckillUserDao.updateLoginCount(users);
        //验证
        String formPass = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        //验证用户
        SeckillUser user = getById(Long.parseLong(mobile));
        if(user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOTEXIST);

        }
        System.out.println(user.toString());
        //验证密码
        String dbPass = user.getPwd();
        System.out.println("数据库中的密码："+dbPass);
        String dbsalt = user.getSalt();
        System.out.println("数据库中的盐："+dbsalt);
        //计算二次md5

        String tmppass= MD5Util.formPassToDBPass(formPass,dbsalt);
        System.out.println("formPass:" + formPass);
        System.out.println("tmppass:" + tmppass);

        if(!tmppass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token= UUIDUtil.uuid();
        addCookie(user,token,response);

        //每次登录都会产生不同的token，设置到redis缓存中
        redisService.set(SeckillUserKey.token,token,user);
        Cookie cookie =new Cookie(COOKIE1_NAME_TOKEN,token);
        cookie.setMaxAge(3600);//1小时与session'一致
        //网站根目录
        cookie.setPath("/");
        response.addCookie(cookie);
        return true;
    }
    public boolean register(HttpServletResponse response, RegisterVo loginVo){
        System.out.println("开始注册");
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        //验证
        String formPass = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        //验证密码
        //计算二次md5
        String dbsalt="1q2w3e4r";
        String tmppass= MD5Util.formPassToDBPass(formPass,dbsalt);
        System.out.println("formPass:" + formPass);
        System.out.println("tmppass:" + tmppass);
        loginVo.setPassword(tmppass);
        loginVo.setNickname("user"+mobile.substring(9,11));
        loginVo.setSalt(dbsalt);
        loginVo.setRegister_date(new Date());
        boolean insert = userDao.insert_into(loginVo);
        System.out.println("插入结果："+insert);
        return insert;
    }
    /*
        添加cookie或者叫更新cookie
     */
    public void addCookie(SeckillUser  user,String token,HttpServletResponse response){
        System.out.println("uuid: "+token);
        //将uuid写入到对应的cookie中，传递给客户端，
        redisService.set(SeckillUserKey.token,token,user);
        Cookie cookie =new Cookie(COOKIE1_NAME_TOKEN,token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());//1小时
        //网站根目录
        cookie.setPath("/");
        //写cookie
        response.addCookie(cookie);
    }
    /**
     * 从缓存中获取token
     */
    public SeckillUser getByToken(String token,HttpServletResponse response){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        SeckillUser user = redisService.get(SeckillUserKey.token,token,SeckillUser.class);
        //再次请求的时候延长有效期，重设缓存里的值
        if(user!=null){
            System.out.println(token);
            addCookie(user,token,response);
        }
        return user;
    }
    public String loginString(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo==null) {
            return CodeMsg.SERVER_ERROR.getMsg();
        }
        //经过了依次MD5的密码
        String mobile=loginVo.getMobile();
        String formPass=loginVo.getPassword();
        //判断手机号是否存在
        SeckillUser user=getById(Long.parseLong(mobile));
        //查询不到该手机号的用户
        if(user==null) {
            return CodeMsg.MOBILE_NOTEXIST.getMsg();
        }
        //手机号存在的情况，验证密码，获取数据库里面的密码与salt去验证
        //111111--->e5d22cfc746c7da8da84e0a996e0fffa
        String dbPass=user.getPwd();
        String dbSalt=user.getSalt();
        System.out.println("dbPass:"+dbPass+"   dbSalt:"+dbSalt);
        //验证密码，计算二次MD5出来的pass是否与数据库一致
        String tmppass=MD5Util.formPassToDBPass(formPass, dbSalt);
        System.out.println("formPass:"+formPass);
        System.out.println("tmppass:"+tmppass);
        if(!tmppass.equals(dbPass)) {
            return CodeMsg.PASSWORD_ERROR.getMsg();
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(user,token,response);
//		return CodeMsg.SUCCESS;
        return token;
    }
}
