package com.seckill.service;


import com.seckill.pojo.OrderInfo;
import com.seckill.pojo.SeckillUser;
import com.seckill.redis.RedisService;
import com.seckill.redis.SeckillKey;
import com.seckill.result.CodeMsg;
import com.seckill.result.Result;
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
    @Autowired
    RedisService redisService;
    //事务操作
    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
        //减少库存
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            OrderInfo orderInfo = orderService.createOrder(user, goods);
            return orderInfo;
        }else {
            //做一个标记，代表商品已经秒杀完了。
            setGoodsOver(goods.getId());
            return null;
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
    }
}
