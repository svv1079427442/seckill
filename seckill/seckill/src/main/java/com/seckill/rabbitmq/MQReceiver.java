package com.seckill.rabbitmq;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {
    private static Logger log= LoggerFactory.getLogger(MQReceiver.class);

	@Autowired
	RedisService redisService;
	@Autowired
	GoodsService goodsService;
	@Autowired
	OrderService orderService;
	@Autowired
	SeckillService seckillService;

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
		SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);
		SeckillUser seckillUser = seckillMessage.getUser();
		long goodsId = seckillMessage.getGoodsId();
		//先判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//入队消息（一般很少的数据可以进来）访问数据库
		int store=goods.getStockCount();
		System.out.println("当前库存还有："+store);
		if(store<=0){
			return;
		}
		SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(),goodsId);
		//注！ 也可以不去判断因为数据库里面已经加入了唯一索引。但是用的是redis也不太影响性能
		if(order!=null){//用户已经秒杀，防止重复秒杀
			return;
		}
		//减库存//下订单//写入秒杀订单
		seckillService.seckill(seckillUser,goods);
		
	}


/*
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)//指明监听的是哪一个queue
	public void receiveTopic1(String message) {
		log.info("receiveTopic1 message:"+message);
	}

	@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)//指明监听的是哪一个queue
	public void receiveTopic2(String message) {
		log.info("receiveTopic2 message:"+message);
	}

	@RabbitListener(queues=MQConfig.HEADER_QUEUE)//指明监听的是哪一个queue
	public void receiveHeaderQueue(byte[] message) {
		log.info("receive Header Queue message:"+new String(message));
	}
*/




}
