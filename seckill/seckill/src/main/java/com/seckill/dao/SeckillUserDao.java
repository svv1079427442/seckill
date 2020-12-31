package com.seckill.dao;

import com.seckill.pojo.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface SeckillUserDao {
    @Select("select * from seckill_user where id=#{id}")
    public SeckillUser getById(@Param("id") long id);
    @Select("update seckill_user set password=#{password} where id=#{id}")
    public void update(SeckillUser seckillUser);
    @Update("update seckill_user set login_count=#{loginCount},last_login_date=SYSDATE() where id=#{id}")
    public int updateLoginCount(SeckillUser seckillUser);
}
