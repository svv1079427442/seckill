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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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
        if(user == null || goodsId <= 0){
            return null;
        }
        //随机生成字符串
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SeckillKey.getMiaoshaPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }

    public BufferedImage createVerifyCode(SeckillUser user, long goodsId) {
        if(user == null || goodsId <= 0){
            return null;
        }
        int width=80;
        int height = 33;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        //背景颜色填充
        g.setColor(new Color(0xDCDCDC));
        //边框黑色
        g.setColor(Color.BLACK);
        g.drawRect(0,0,width-1,height-1);
        Random random = new Random();
        //生成50个干扰点
        for(int i = 0;i <50;i++){
            int x=random.nextInt(width);
            int y = random.nextInt(height);
            g.drawOval(x,y,0,0);
        }
        //生成验证码
        String verifyCode = generateVerifyCode(random);
        g.setColor(new Color(255,20,147));
        g.setFont(new Font("Candara",Font.BOLD,24));
        g.drawString(verifyCode,8,24);
        g.dispose();
        //验证码存入redis中
        int rnd =calc(verifyCode);
        redisService.set(SeckillKey.getSeckillVertifyCode,user.getId()+","+goodsId,rnd);
        //将图片输出
        return image;
    }
    private static char[] ops = new char[]{'+','-','*'};
    /**
     * 只做加减乘
     * @param random
     * @return
     */

    private String generateVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }
    public static void main(String[] args) {
        System.out.println(calc("1+2-1"));
    }
    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }



}
