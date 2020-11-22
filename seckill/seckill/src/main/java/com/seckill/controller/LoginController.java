package com.seckill.controller;

import com.seckill.pojo.SeckillUser;
import com.seckill.redis.RedisService;
import com.seckill.result.Result;
import com.seckill.service.SeckillUserService;
import com.seckill.service.UserService;
import com.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @Autowired
    SeckillUserService seckillUserService;
    //slf日志
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String toLogin(SeckillUser user, Model model) {
        if (user != null) {
            model.addAttribute("user", user);
            return "goods_list";
        } else {
            return "login";//返回页面login

        }

    }
    @RequestMapping("/do_login")//异步登录
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response,@Valid LoginVo loginVo){
        System.out.println("开始登录");
        log.info(loginVo.toString());
        seckillUserService.login(response,loginVo);
        return Result.success(true);
    }
    //使用JSR303校验
    @RequestMapping("/do_login_test")//作为异步操作
    @ResponseBody
    public Result<String> doLogintest(HttpServletResponse response, @Valid LoginVo loginVo) {//0代表成功
        String token=seckillUserService.loginString(response,loginVo);
        return Result.success(token);
    }
}