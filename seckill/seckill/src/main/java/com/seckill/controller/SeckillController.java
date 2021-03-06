package com.seckill.controller;

import com.seckill.access.AccessLimit;
import com.seckill.pojo.SeckillOrder;
import com.seckill.pojo.SeckillUser;
import com.seckill.rabbitmq.MQSender;
import com.seckill.rabbitmq.SeckillMessage;
import com.seckill.redis.GoodsKey;
import com.seckill.redis.RedisService;
import com.seckill.result.CodeMsg;
import com.seckill.result.Result;
import com.seckill.service.GoodsService;
import com.seckill.service.OrderService;
import com.seckill.service.SeckillService;
import com.seckill.service.SeckillUserService;

import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
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
    @Autowired
    MQSender mqSender;

    private Map<Long,Boolean> localOverMap =new HashMap<Long, Boolean>();
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null)  {
            return;
        }
        for (GoodsVo goods: goodsList){
            redisService.set(GoodsKey.getSeckillGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }



    @RequestMapping(value = "/{path}/do_seckill",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> seckill(SeckillUser user, Model model,
                                   @RequestParam(value = "goodsId",defaultValue = "0") long goodsId,
                                   @PathVariable("path") String path) {
        System.out.println("*************************"+ "开始验证秒杀地址" +"*************************");
        model.addAttribute("user",user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);//返回页面login
        }
        //验证path
        boolean check = seckillService.checkPath(user,goodsId,path);
        System.out.println("验证结果：" + check);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEAGAL);
        }
        //内存标记，减少redis访问
        System.out.println("获取的goodsId为："+goodsId);
        boolean over = localOverMap.get(goodsId);
        //over为true，直接返回秒杀完毕，库存不足
        if(over){
            return Result.error(CodeMsg.MIAOSHA_OVER_ERROR);
        }
        //判断是否秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order!=null){//用户已经秒杀，防止重复秒杀
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //redis预减库存
        Long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);
        System.out.println("同一用户执行了第二次");
        System.out.println("库存数量stock："+stock);
        //判断库存数量
        if(stock < 0){
            //置为true代表redis库存空了
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_OVER_ERROR);
        }

        //入队
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(seckillMessage);
        return Result.success(0);//0代表排队中

    }

    /**
     * 隐藏秒杀接口
     * @param user
     * @param model
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(HttpServletRequest request, SeckillUser user, Model model,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam(value = "vertifyCode" ,required = false,defaultValue = "0") int vertifyCode) {
        System.out.println("********拦截器拦截请求，获取到秒杀地址*********");
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);//返回页面login
        }
        //验证验证码
        System.out.println("商品id："+goodsId);
        System.out.println("验证码输出："+vertifyCode);
        boolean check=seckillService.checkVCode(user, goodsId,vertifyCode );
        System.out.println("验证结果:"+check);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEAGAL);
        }
        System.out.println("通过!");
        String path = seckillService.createSeckillPath(user,goodsId);
        return Result.success(path);

    }
    /**
     * 客户端做一个轮询，查看是否成功与失败，失败了则不用继续轮询。
     * 秒杀成功，返回订单的Id。
     * 库存不足直接返回-1。
     * 排队中则返回0。
     * 查看是否生成秒杀订单。
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> doSeckillResult(Model model, SeckillUser user,
                                        @RequestParam(value = "goodsId", defaultValue = "0") long goodsId) {
        long result=seckillService.getSeckillResult(user.getId(),goodsId);
        System.out.println("轮询 result："+result);

        return Result.success(result);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCode(Model model, SeckillUser user,
                                        @RequestParam(value = "goodsId") long goodsId, HttpServletResponse response) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);//返回页面login
        }
        //生成验证码
        BufferedImage image = seckillService.createVerifyCode(user,goodsId);
        try {
            OutputStream out=response.getOutputStream();
            //输出验证码
            ImageIO.write(image,"JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }
   /*
    //post与get区别，get是幂等的无论调用多少次，服务端都一样，从服务端获取数据
    //post不是幂等的，向服务端提交数据。
    @RequestMapping(value = "/do_seckill",method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> list(SeckillUser user, Model model
                          *//*@RequestParam("goodsId") long goodsId*//*) {
        long goodsId=1;
        model.addAttribute("user",user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);//返回页面login
        }
        //先判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int store=goods.getStockCount();
        System.out.println("当前库存还有："+store);
        if(store<=0){
            System.out.println("********************sssssssssssss"+"没库存了");
            //model.addAttribute("errormsg", CodeMsg.MIAOSHA_OVER_ERROR.getMsg());//秒杀完毕
            return Result.error(CodeMsg.MIAOSHA_OVER_ERROR);
        }
        //判断是否秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goods.getId());
        if(order!=null){//用户已经秒杀，防止重复秒杀
            //System.out.println(order.toString());
            //model.addAttribute("errormsg",CodeMsg.REPEATE_MIAOSHA.getMsg());
            //System.out.println("用户重复秒杀");
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存//下订单//写入秒杀订单
        System.out.println("开始秒杀！！！");
        OrderInfo orderInfo=seckillService.seckill(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return Result.success(orderInfo);
    }
*/

}
