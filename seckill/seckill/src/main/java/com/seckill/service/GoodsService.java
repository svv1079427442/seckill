package com.seckill.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seckill.dao.AdminDao;
import com.seckill.dao.GoodsDao;
import com.seckill.pojo.*;
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

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public List<Goods> listGoods() {
        return adminDao.getGoodsList();
    }
    public List<SeckillGoods> seckillGoods() {
        return adminDao.getSeckillGoods();
    }
    public List<OrderInfo> orderDetail() {
        return adminDao.getOrderDetail();
    }
    public List<SeckillOrder> seckillOrder() {
        return adminDao.getSeckillOrder();
    }
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        SeckillGoods g = new SeckillGoods();
        g.setGoodsId(goods.getId());
        return goodsDao.reduceStock(g);
    }

    public List<Goods> findPage(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return adminDao.getGoodsList();
    }

    public int del_goods(int id) {
        return adminDao.delGoods(id);
    }

    public Goods getById(int id) {

        return adminDao.getById(id);
    }

    public int update(int id, String goods_name, String goods_title, BigDecimal goods_price, int goods_stock) {
        return adminDao.update(id, goods_name, goods_title, goods_price, goods_stock);
    }

    public PageInfo<Goods> getGoodsWithPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> goodsList = adminDao.getGoodsList();
        return new PageInfo<>(goodsList, 5);
    }

    public PageInfo<SeckillGoods> getSeckillGoods(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SeckillGoods> goodsList = adminDao.getSeckillGoods();
        return new PageInfo<>(goodsList, 5);
    }
    public PageInfo<OrderInfo> getOrderDetail(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderInfo> goodsList = adminDao.getOrderDetail();
        return new PageInfo<>(goodsList, 5);
    }
    public PageInfo<SeckillOrder> getSeckillOrder(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SeckillOrder> goodsList = adminDao.getSeckillOrder();
        return new PageInfo<>(goodsList, 5);
    }
}