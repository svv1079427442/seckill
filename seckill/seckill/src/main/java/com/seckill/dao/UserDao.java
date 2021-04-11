package com.seckill.dao;

import com.seckill.pojo.SeckillUser;
import com.seckill.pojo.User;
import com.seckill.vo.LoginVo;
import com.seckill.vo.RegisterVo;
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
    @Insert("insert into seckill_user (id,pwd,nickname,salt,register_date) values(#{mobile},#{password},#{nickname},#{salt},#{register_date})")
    public boolean insert_into(RegisterVo loginVo);
    @Select("select id from seckill_user where id = #{id}")
    public SeckillUser getByMobile(@Param("id")long id);
}
