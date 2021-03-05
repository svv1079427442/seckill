package com.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@EnableAutoConfiguration
@ComponentScan({"com.seckill","com.seckill.config"})
public class SeckillMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillMainApplication.class,args);
    }
}
