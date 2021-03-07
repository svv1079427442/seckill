package com.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@EnableAutoConfiguration
@ComponentScan({"com.seckill","com.seckill.config"})
public class SeckillMainApplication extends WebMvcConfigurerAdapter {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }
    public static void main(String[] args) {
        SpringApplication.run(SeckillMainApplication.class,args);
    }
}
