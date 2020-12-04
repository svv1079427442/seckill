package com.seckill.controller;

import com.seckill.pojo.SeckillUser;
import com.seckill.result.Result;
import com.seckill.service.SeckillUserService;
import com.seckill.vo.LoginVo;
import com.seckill.vo.RegisterVo;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    private SeckillUserService seckillUserService;
    //slf日志
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_register")
    public String toLogin(SeckillUser user, Model model) {
            return "register";//返回页面login
    }
    @RequestMapping("/do_register")
    @ResponseBody
    public Result<Boolean> do_register(HttpServletResponse response, @Valid RegisterVo loginVo){
        System.out.println("开始注册");
        log.info(loginVo.toString());
        seckillUserService.register(response,loginVo);
        return Result.success(true);
    }
}
