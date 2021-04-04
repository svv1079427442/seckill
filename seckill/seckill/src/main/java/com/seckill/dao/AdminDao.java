package com.seckill.dao;

import com.seckill.pojo.*;
import com.seckill.vo.GoodsVo;
import com.seckill.vo.RegisterVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Mapper
public interface AdminDao {
    @Select("select * from admin where name=#{name}")
    public Admin getByName(@Param("name") String name);
    @Insert("insert into admin(name,password) values(#{name},#{password})")
    public void insert_admin(Admin admin);
    @Select("select * from goods")
    public List<Goods> getGoodsList();
    @Select("select * from admin")
    public List<Admin> getAdminList();
    @Delete("delete from goods where id =#{id}")
    public int delGoods(@Param("id") int id);
    @Select("select * from goods where id=#{id}")
    public Goods getById(@Param("id") int id);
    @Update("update goods set id=#{id},goods_name=#{goods_name},goods_title=#{goods_title},goods_price=#{goods_price},goods_stock=#{goods_stock} where id =#{id}")
    public int update(@Param("id") int id, @Param("goods_name") String goods_name, @Param("goods_title") String goods_title, @Param("goods_price") BigDecimal goods_price, @Param("goods_stock") int goods_stock);
    @Select("select * from seckill_goods")
    public List<SeckillGoods> getSeckillGoods();
    @Select("select * from order_info")
    public List<OrderInfo> getOrderDetail();
    @Select("select * from seckill_order")
    public List<SeckillOrder> getSeckillOrder();
    @Select("select * from seckill_user")
    public List<SeckillUser> getSeckillUser();
    @Insert("insert into goods(goods_name,goods_img,goods_stock,goods_price,goods_title) values(#{goods_name},#{goods_img},#{goods_stock},#{goods_price},#{goods_detail})")
    public void add_goods(@Param("goods_name") String goods_name, @Param("goods_title") String goods_title, @Param("goods_price") BigDecimal goods_price,@Param("goods_stock") int goods_stock,@Param("goods_img") String goods_img);
    @Select("select * from goods where goods_title like '%${name}%'")
    public List<Goods> getByGoodsName(@Param("name") String name);
    /* @Insert("insert into seckill_user (id,pwd,nickname,salt,register_date) values(#{mobile},#{password},#{nickname},#{salt},#{register_date})")
    public boolean insert_into(RegisterVo loginVo);*/
}
