package com.seckill.service;

import com.seckill.dao.SeckillUserDao;
import com.seckill.dao.UserDao;
import com.seckill.pojo.Goods;
import com.seckill.pojo.User;
import com.seckill.vo.SeckillGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    UserDao userDao;
    @Autowired
    SeckillUserDao seckillUserDao;
    public User getById(int id){
        return userDao.getById(id);
    }
    public List<SeckillGoodsVo> getNameById(){
        return seckillUserDao.getGoodsName();
    }
    public List<SeckillGoodsVo> search_res(String name){
        return seckillUserDao.search_res(name);
    }
}
