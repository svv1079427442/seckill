package com.seckill.config;

import com.seckill.pojo.SeckillUser;
import com.seckill.service.SeckillUserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Service
public class UserArgumentResolvers implements HandlerMethodArgumentResolver {
    @Autowired
    SeckillUserService seckillUserService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz== SeckillUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        String paramToken=request.getParameter(SeckillUserService.COOKIE1_NAME_TOKEN);
        String cookieToken=getCookieValue(request,SeckillUserService.COOKIE1_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            System.out.println("进来了++++++++");
            return null;
        }
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return seckillUserService.getByToken(token,response);

    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        //遍历request里面所有的cookie
        Cookie[] cookies=request.getCookies();
        if(cookies!=null) {
            for(Cookie cookie :cookies) {
                if(cookie.getName().equals(cookieName)) {
                    System.out.println("getCookieValue:"+cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        System.out.println("No getCookieValue!");
        return null;
    }
}
