package com.seckill.service;


import com.seckill.pojo.OrderInfo;
import com.seckill.pojo.SeckillOrder;
import com.seckill.pojo.SeckillUser;
import com.seckill.redis.RedisService;
import com.seckill.redis.SeckillKey;
import com.seckill.util.MD5Util;
import com.seckill.util.UUIDUtil;
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
        //减少库存  操作数据库
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

    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if(order !=null){//秒杀成功
            return order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return  -1; //失败
            }else {
                return 0;//排队中
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return  redisService.exitsKey(SeckillKey.isGoodsOver, ""+goodsId);
    }

    public boolean checkPath(SeckillUser user, long goodsId, String path) {
        if(user == null || path == null){
            return false;
        }
        String redisPath = redisService.get(SeckillKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,String.class);
        return path.equals(redisPath);
    }

    public String createSeckillPath(SeckillUser user, long goodsId) {
        //随机生成字符串
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SeckillKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }
}
