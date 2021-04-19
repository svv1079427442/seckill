package com.seckill.dao;

import com.seckill.pojo.Goods;
import com.seckill.pojo.SeckillUser;
import com.seckill.vo.SeckillGoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface SeckillUserDao {
    @Select("select * from seckill_user where id=#{id}")
    public SeckillUser getById(@Param("id") long id);
    @Select("update seckill_user set password=#{password} where id=#{id}")
    public void update(SeckillUser seckillUser);
    @Update("update seckill_user set login_count=#{loginCount},last_login_date=SYSDATE() where id=#{id}")
    public int updateLoginCount(SeckillUser seckillUser);
    @Select("SELECT g.goods_name,sg.* from goods g RIGHT JOIN seckill_goods sg ON sg.goods_id=g.id;")
    public List<SeckillGoodsVo> getGoodsName();
    @Select("SELECT g.goods_name,sg.* from goods g RIGHT JOIN seckill_goods sg ON sg.goods_id=g.id where g.goods_name like '%${name}%' or g.goods_name like '${name}%';;")
    public List<SeckillGoodsVo> search_res(@Param("name") String name);

}
