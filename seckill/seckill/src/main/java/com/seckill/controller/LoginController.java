package com.seckill.controller;

import com.seckill.pojo.SeckillUser;
import com.seckill.redis.RedisService;
import com.seckill.result.Result;
import com.seckill.service.SeckillUserService;
import com.seckill.service.UserService;
import com.seckill.vo.LoginAdminVo;
import com.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

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
            return "login";//返回页面login
    }
    @RequestMapping("/do_login")//异步登录
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response,@Valid LoginVo loginVo){
        System.out.println("开始登录");
        log.info(loginVo.toString());
        seckillUserService.login(response,loginVo);
        return Result.success(true);
    }
    @RequestMapping("/to_login_admin")
    public String toLogin_admin(SeckillUser user, Model model,HttpServletResponse response) throws IOException {
        //response.getWriter().write("1");
        System.out.println("调用****************");
        return "login_admin";//返回页面login
    }

    /**
     * 管理员登录
     * @param response
     * @param loginAdminVo
     * @return
     */
    @RequestMapping("/do_login_admin")//异步登录
    @ResponseBody
    public Result<Boolean> doLogin_admin(HttpServletResponse response, HttpServletRequest request, @Valid LoginAdminVo loginAdminVo, Model model){
        System.out.println("+++++++++++++++++++++++++++++开始管理员登录++++++++++++++++++++++++++++++++++++++");
        log.info(loginAdminVo.toString());
        seckillUserService.login_admin(response,loginAdminVo);
        HttpSession session = request.getSession();
        session.setAttribute("admin", loginAdminVo.getName());
        System.out.println("管理员信息："+loginAdminVo.toString());
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