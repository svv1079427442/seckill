package com.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/Demo")
public class DemoController {
    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello world!";
    }
}
