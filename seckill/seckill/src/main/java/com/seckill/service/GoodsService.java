package com.seckill.service;

import com.seckill.dao.GoodsDao;
import com.seckill.pojo.Goods;
import com.seckill.pojo.SeckillGoods;
import com.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }


    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public void reduceStock(GoodsVo goods) {
        SeckillGoods g=new SeckillGoods();
        g.setGoodsId(goods.getId());
        goodsDao.reduceStock(g);
    }
}
