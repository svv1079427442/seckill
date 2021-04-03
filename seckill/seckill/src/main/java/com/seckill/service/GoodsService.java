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

    public List<Goods> getList() {
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

    public PageInfo getGoodsWithPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> goodsList = adminDao.getGoodsList();
        return new PageInfo<>(goodsList);
    }

    public PageInfo<SeckillGoods> getSeckillGoods(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SeckillGoods> goodsList = adminDao.getSeckillGoods();
        return new PageInfo<>(goodsList);
    }
    public PageInfo<OrderInfo> getOrderDetail(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderInfo> goodsList = adminDao.getOrderDetail();
        return new PageInfo<>(goodsList);
    }
    public PageInfo<SeckillOrder> getSeckillOrder(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SeckillOrder> goodsList = adminDao.getSeckillOrder();
        return new PageInfo<>(goodsList);
    }

    public PageInfo<SeckillUser> getSeckillUser(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<SeckillUser> goodsList = adminDao.getSeckillUser();
        return new PageInfo<>(goodsList);
    }

    /**
     * 添加商品@Param("goods_name") String goods_name, @Param("goods_detail") String goods_detail, @Param("goods_price") BigDecimal goods_price,@Param("goods_stock") int goods_stock
     */
    public void addGood(String goods_name,String goods_title,BigDecimal goods_price,int goods_stock,String goods_img){
        adminDao.add_goods(goods_name, goods_title, goods_price, goods_stock, goods_img);
    }
    public List<Goods> getByGoodsName(String name){
        return adminDao.getByGoodsName(name);
    }
    public Admin getByAdName(String name){
      return   adminDao.getByName(name);
    }
}