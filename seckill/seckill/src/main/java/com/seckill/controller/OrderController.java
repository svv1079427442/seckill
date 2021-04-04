package com.seckill.controller;

import com.seckill.pojo.OrderInfo;
import com.seckill.pojo.SeckillUser;
import com.seckill.redis.RedisService;
import com.seckill.result.CodeMsg;
import com.seckill.result.Result;
import com.seckill.service.GoodsService;
import com.seckill.service.OrderService;
import com.seckill.vo.GoodsVo;
import com.seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, SeckillUser user,
                        @RequestParam("orderId") long orderId){

        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo =new OrderDetailVo();
        vo.setGoodsVo(goods);
        vo.setOrder(order);
        //System.out.println("*********srrr*****"+order.toString());
        return Result.success(vo);
    }

    /**
     * 支付
     * @param model
     * @param info
     * @return
     */
    @RequestMapping(value = "/pay_status", method = RequestMethod.GET)
    @ResponseBody
    public Result<Integer> pay(Model model, OrderInfo info, HttpServletRequest request) {
        String status = request.getParameter("status");
        int result = orderService.update_status(Integer.parseInt(status));
        return Result.success(result);
    }
}
