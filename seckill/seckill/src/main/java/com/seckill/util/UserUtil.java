package com.seckill.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seckill.pojo.SeckillUser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class UserUtil {
    private static void createUser(int count) throws Exception{
        List<SeckillUser> users=new ArrayList<SeckillUser>();
        //生成用户
        for(int i=0;i<count;i++) {
            SeckillUser user=new SeckillUser();

            long l = System.currentTimeMillis();
            user.setId(17300000000L+i);//注意，两数相加，不会超出数位数
            user.setLoginCount(1);
            user.setNickname("user"+i);
            user.setRegisterDate(new Date());
            user.setLastLoginDate(new Date());
            user.setLoginCount(1);
            user.setHead("/user/useri.png");
            user.setSalt("1q2w3e4r");
            user.setPwd(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setDeliveryaddress("上海市徐汇区");
            users.add(user);
        }
        System.out.println("craete users ----insert to db");
        //插入数据库
        Connection conn=DBUtil.getConn();
        String sql="insert into seckill_user (login_count,nickname,register_date,salt,pwd,id,head,last_login_date,delivery_address) values"
                + "(?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt=conn.prepareStatement(sql);

        //生成用户
        for(int i=0;i<users.size();i++) {

            SeckillUser user=users.get(i);
            pstmt.setInt(1, user.getLoginCount());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4, user.getSalt());
            pstmt.setString(5, MD5Util.inputPassToDBPass("123456", user.getSalt()));
            pstmt.setLong(6,user.getId());
            pstmt.setString(7, user.getHead());
            pstmt.setTimestamp(8,new Timestamp(user.getLastLoginDate().getTime()));
            pstmt.setString(9,user.getDeliveryaddress());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
        System.out.println("登录，生成token");
        //登录，使之生成一个token
        String urlString="http://localhost:8080/login/do_login_test";
        File file=new File("D:/tokens.txt");
        if(file.exists()) {
            file.delete();
        }
        RandomAccessFile raf=new RandomAccessFile(file,"rw");
        file.createNewFile();
        raf.seek(0);
        for(int i=0;i<users.size();i++) {
            SeckillUser user=users.get(i);
            URL url=new URL(urlString);
            HttpURLConnection co=(HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out=co.getOutputStream();
            String params="mobile="+user.getId()+"&password="
                    +MD5Util.formPassToDBPass("123456", user.getSalt());
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream=co.getInputStream();
            ByteArrayOutputStream bout=new ByteArrayOutputStream();
            byte buff[]=new byte[1024];
            int len=0;
            while((len=inputStream.read(buff))>=0) {
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            String response=new String(bout.toByteArray());
            JSONObject jo= JSON.parseObject(response);
            String token=jo.getString("data");
            System.out.println("user:"+user.getId()+"	token:"+token);
            String row=user.getId()+","+token;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : "+user.getId());
        }
    }
    public static void main(String[] args) throws Exception {
        //createUser(5000);
        createUser(1000);

    }
}
