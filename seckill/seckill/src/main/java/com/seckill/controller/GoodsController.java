package com.seckill.controller;

import com.seckill.pojo.SeckillUser;
import com.seckill.redis.GoodsKey;
import com.seckill.redis.RedisService;
import com.seckill.service.GoodsService;
import com.seckill.service.SeckillUserService;
import com.seckill.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    public static final String COOKIE1_NAME_TOKEN="token";

    @Autowired
    RedisService redisService;
    @Autowired
    SeckillUserService seckillUserService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    ApplicationContext applicationContext;
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request,HttpServletResponse response,Model model, SeckillUser user){
        /*//通过取到cookie，首先取@RequestParam没有再去取@CookieValue
        if( StringUtils.isEmpty(paramToken) && StringUtils.isEmpty(cookieToken)){
            return "login";
        }
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        SeckillUser user=seckillUserService.getByToken(token,response);*/
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)){//不为空直接返回
            return html;
        }
        model.addAttribute("user",user);
        //查询商品列表
        List<GoodsVo> list = goodsService.listGoodsVo();
        model.addAttribute("goodsList",list);
        //return "goods_list";

        SpringWebContext springWebContext=new SpringWebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap(),applicationContext);
        //手动渲染
        html=thymeleafViewResolver.getTemplateEngine().process("goods_list",springWebContext);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }
    //测试使用
    @RequestMapping("/to_list_test")
    public String list_test(Model model,@CookieValue(value = SeckillUserService.COOKIE1_NAME_TOKEN)String cookieToken,
                            @RequestParam(value = SeckillUserService.COOKIE1_NAME_TOKEN)String paramToken, HttpServletResponse response
            ,SeckillUser user){
        model.addAttribute("user",user);
        return "goods_list";
    }
    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String todetail(HttpServletRequest request,HttpServletResponse response,Model model, SeckillUser user,
                           @PathVariable("goodsId")long goodsId){
        //snowflake算法生成唯一id
        model.addAttribute("user",user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
        if(!StringUtils.isEmpty(html)){//不为空直接返回
            return html;
        }

        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);
        int seckillStatus=0;
        int remainSeconds=0;//秒杀倒计时 start-now = remaintime  还剩多长时间
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        if(now<startAt){
            seckillStatus=0;//秒杀未开始
            remainSeconds=(int)((startAt-now)/100);
        }else if (now>endAt){
            seckillStatus=2;//秒杀已结束
            remainSeconds=-1;
        }else {
            seckillStatus=1;//秒杀进行中
            remainSeconds=0;
        }
        model.addAttribute("seckillStatus",seckillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        //return "goods_detail";
        //手动渲染
        SpringWebContext springWebContext=new SpringWebContext(request,response,request.getServletContext(),
                                                                    request.getLocale(),model.asMap(),applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",springWebContext);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return  html;
    }
}
