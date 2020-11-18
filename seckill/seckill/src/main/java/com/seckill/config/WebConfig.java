package com.seckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * 实现自定义的参数解析器
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    UserArgumentResolvers userArgumentResolvers;
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolvers);
    }
}
