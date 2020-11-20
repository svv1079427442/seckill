package com.seckill.controller;

import com.seckill.pojo.SeckillUser;
import com.seckill.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/User")
public class DemoController {
    @RequestMapping("/info")
    @ResponseBody
    public Result<SeckillUser> info(Model model,SeckillUser user){
        model.addAttribute("user",user);
        return Result.success(user);
    }
}
