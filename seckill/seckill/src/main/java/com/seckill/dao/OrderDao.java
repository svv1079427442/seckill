package com.seckill.dao;

import com.seckill.pojo.OrderInfo;
import com.seckill.pojo.SeckillOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {
    @Select("select * from seckill_order where user_id=#{userId} and goods_id=#{goodsId}")
    SeckillOrder getSeckillOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);
    @Insert("insert into order_info(user_id,user_name,goods_id,goods_name,goods_count,goods_price,order_channel,status,create_date)value(" +
            "#{userId},#{userName},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = long.class,before = false,statement = "select last_insert_id()")
    public long insert(OrderInfo orderInfo);
    @Insert("insert into seckill_order(user_id,goods_id,order_id)values(#{userId},#{goodsId},#{orderId})")
    public void insertSeckillOrder(SeckillOrder seckillOrder);
    @Select("select * from order_info where id =#{orderId}")
    OrderInfo getOrderById(long orderId);
    @Update("update order_info set status = #{status}")
    public int update_status(int status);
    @Update("update order_info set delivery_address = #{address} where user_id = #{id}")
    public int update_address(@Param("address")String address,@Param("id")long id);
    @Select("select * from order_info where user_id =#{id}")
    public OrderInfo getOrderByUserId(@Param("id") long id);
    //public long select_orderId()














}
