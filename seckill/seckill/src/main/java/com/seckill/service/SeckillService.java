package com.seckill.service;


import com.seckill.pojo.OrderInfo;
import com.seckill.pojo.SeckillUser;
import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    //事务操作
    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
        //减少库存
        goodsService.reduceStock(goods);

        OrderInfo orderInfo=orderService.createOrder(user,goods);

        return orderInfo;
    }
}
