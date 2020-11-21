package com.seckill.redis;

public class GoodsKey extends BasePrefix{
    private GoodsKey(int expreSeconds,String prefix){
        super(expreSeconds,prefix);
    }
    //商品列表
    public static GoodsKey getGoodsList=new GoodsKey(60,"gl");
    //商品详情
    public static GoodsKey getGoodsDetail=new GoodsKey(60,"gd");
}
