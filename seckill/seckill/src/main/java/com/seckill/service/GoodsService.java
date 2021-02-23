package com.seckill.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seckill.dao.AdminDao;
import com.seckill.dao.GoodsDao;
import com.seckill.pojo.Admin;
import com.seckill.pojo.Goods;
import com.seckill.pojo.SeckillGoods;
import com.seckill.redis.RedisService;
import com.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;
    @Autowired
    AdminDao adminDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }
    public List<Goods> listGoods(){
        return adminDao.getGoodsList();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        SeckillGoods g=new SeckillGoods();
        g.setGoodsId(goods.getId());
        return goodsDao.reduceStock(g);
    }
    public List<Goods> findPage(int page, int pageSize){
        PageHelper.startPage(page,pageSize);
        return adminDao.getGoodsList();
    }
    public int del_goods(int id){
        return adminDao.delGoods(id);
    }

    public Goods getById(int id){

        return adminDao.getById(id);
    }
    public Goods update(int id, String goods_name, BigDecimal goods_price, int goods_stock){
        return adminDao.update(id, goods_name, goods_price, goods_stock);
    }
}
