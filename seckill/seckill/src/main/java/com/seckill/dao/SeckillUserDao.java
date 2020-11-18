package com.seckill.dao;

import com.seckill.pojo.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



@Mapper
public interface SeckillUserDao {
    @Select("select * from seckill_user where id=#{id}")
    public SeckillUser getById(@Param("id") long id);
    @Select("update seckill_user set password=#{password} where id=#{id}")
    public void update(SeckillUser seckillUser);
}
