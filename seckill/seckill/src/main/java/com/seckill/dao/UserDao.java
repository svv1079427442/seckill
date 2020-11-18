package com.seckill.dao;

import com.seckill.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserDao {
    @Select("select * from seckill_user where id=#{id}")
    public User getById(@Param("id") int id);
    @Insert("insert into t_user(name) values(#{name})")
    public void insert(User user);
}
