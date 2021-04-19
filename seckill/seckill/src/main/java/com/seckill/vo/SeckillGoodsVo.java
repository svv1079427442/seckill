package com.seckill.vo;

import com.seckill.pojo.Goods;

import java.util.Date;

public class SeckillGoodsVo extends Goods {
    private String goodsName;
    private Long id;
    private Long goodsId;
    private Double SeckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    @Override
    public String toString() {
        return "SeckillGoodsVo{" +
                "goodsName='" + goodsName + '\'' +
                ", id=" + id +
                ", goodsId=" + goodsId +
                ", SeckillPrice=" + SeckillPrice +
                ", stockCount=" + stockCount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    @Override
    public String getGoodsName() {
        return goodsName;
    }

    @Override
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Double getSeckillPrice() {
        return SeckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        SeckillPrice = seckillPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
