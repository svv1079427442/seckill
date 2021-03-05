package com.seckill.access;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)//运行期间有效
@Target(METHOD)//注解类型为方法注解
public @interface AccessLimit {
    int seconds();
    int maxCount();
    boolean needLogin() default true;













}
