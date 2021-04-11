package com.seckill.service;

import com.seckill.dao.OrderDao;
import com.seckill.pojo.OrderInfo;
import com.seckill.pojo.SeckillOrder;
import com.seckill.pojo.SeckillUser;
import com.seckill.redis.OrderKey;
import com.seckill.redis.RedisService;
import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;
    public SeckillOrder getSeckillOrderByUserIdGoodsId(long userId, long goodsId) {
        //return orderDao.getSeckillOrderByUserIdGoodsId(userId,goodsId);
        return redisService.get(OrderKey.getSeckillOrderByUidAndGid,""+userId+"_"+goodsId,SeckillOrder.class);
    }
    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
    @Transactional
    public OrderInfo createOrder(SeckillUser user, GoodsVo goods) {
        //设置order表的信息 （详细信息）
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());//获取秒杀价格
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);//0代表未支付
        orderInfo.setUserId(user.getId());
        orderInfo.setUserName(user.getNickname());
        orderDao.insert(orderInfo);
        //设置秒杀order表
        SeckillOrder seckillOrder=new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());

        orderDao.insertSeckillOrder(seckillOrder);
        redisService.set(OrderKey.getSeckillOrderByUidAndGid,""+user.getId()+"_"+goods.getId(),seckillOrder);

        return orderInfo;

    }
    public int update_status(int status){
        return orderDao.update_status(status);
    }

    public int update_address(String address,long id){
        return orderDao.update_address(address,id);
    }

    public OrderInfo getOrderByUserId(long id){
        return orderDao.getOrderByUserId(id);
    }
}
