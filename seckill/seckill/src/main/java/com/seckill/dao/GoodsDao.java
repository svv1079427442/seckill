package com.seckill.dao;

import com.seckill.pojo.Goods;
import com.seckill.pojo.SeckillGoods;
import com.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
@Mapper
public interface GoodsDao {
    @Select("select g.*,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date from seckill_goods sg left join goods g on sg.goods_id=g.id")
    public List<GoodsVo> listGoodsVo();
    @Select("select g.*,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date from seckill_goods sg left join goods g on sg.goods_id=g.id where g.id=#{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@PathVariable("goodsId") long goodsId);
    @Update("update seckill_goods set stock_count = stock_count-1 where goods_id=#{goodsId} and stock_count > 0")
    public boolean reduceStock(SeckillGoods g);
    @Select("select * from goods where id = #{id}")
    public Goods getGoodsImg(@Param("id") long id);
}
