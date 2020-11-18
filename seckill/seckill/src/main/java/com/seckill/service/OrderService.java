package com.seckill.service;

import com.seckill.dao.OrderDao;
import com.seckill.pojo.OrderInfo;
import com.seckill.pojo.SeckillOrder;
import com.seckill.pojo.SeckillUser;
import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;
    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodsId) {
        return orderDao.getSeckillOrderByUserIdGoodsId(userId,goodsId);
    }
    @Transactional
    public OrderInfo createOrder(SeckillUser user, GoodsVo goods) {
        //设置order表的信息 （详细信息）
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());//获取秒杀价格
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);//0代表未支付
        orderInfo.setUserId(user.getId());
        long orderId  = orderDao.insert(orderInfo);
        //设置秒杀order表
        SeckillOrder seckillOrder=new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderId);
        seckillOrder.setUserId(user.getId());
        orderDao.insertSeckillOrder(seckillOrder);

        return orderInfo;

    }
}
