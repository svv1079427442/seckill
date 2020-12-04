package com.seckill.controller;

import com.seckill.pojo.OrderInfo;
import com.seckill.pojo.SeckillOrder;
import com.seckill.pojo.SeckillUser;
import com.seckill.redis.RedisService;
import com.seckill.result.CodeMsg;
import com.seckill.result.Result;
import com.seckill.service.GoodsService;
import com.seckill.service.OrderService;
import com.seckill.service.SeckillService;
import com.seckill.service.SeckillUserService;
import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    RedisService redisService;
    @Autowired
    SeckillUserService seckillUserService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    SeckillService seckillService;
    @RequestMapping(value = "/do_seckill",method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> list(SeckillUser user, Model model
                          /*@RequestParam("goodsId") long goodsId*/) {
        long goodsId=1;
        model.addAttribute("user",user);
        if (user == null) {
            System.out.println("++++++++++++++++返回登陆页面");
            return Result.error(CodeMsg.SESSION_ERROR);//返回页面login
        }
        //先判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int store=goods.getStockCount();
        if(store<0){
            model.addAttribute("errormsg", CodeMsg.MIAOSHA_OVER_ERROR.getMsg());//秒杀完毕
            return Result.error(CodeMsg.MIAOSHA_OVER_ERROR);
        }
        //判断是否秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goods.getId());
        if(order!=null){//用户已经秒杀，防止重复秒杀
            System.out.println(order.toString());
            model.addAttribute("errormsg",CodeMsg.REPEATE_MIAOSHA.getMsg());
            System.out.println("用户重复秒杀");
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存//下订单//写入秒杀订单
        System.out.println("开始秒杀！！！");
        OrderInfo orderInfo=seckillService.seckill(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return Result.success(orderInfo);
    }
}
