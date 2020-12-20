

## 1.商品详情：秒杀倒计时功能：

goods_detail.html

```html
<td>秒杀开始时间</td>
            <td th:text="${#dates.format(goods.startDate,'yyyy-MM-dd HH:mm:ss')}"></td>
            <td id="seckillTip">
                <!-- 先取得这个时间 -->
                <input type="hidden" id="remainSeconds" th:value="${remainSeconds}"></input>
                <span th:if="${seckillStatus eq 0}">距离秒杀开始还剩:<span id="countDown" th:text="${remainSeconds}"></span>秒</span>
                <span th:if="${seckillStatus eq 1}">秒杀正在进行</span>
                <span th:if="${seckillStatus eq 2}">秒杀已经结束</span>
            </td>
```

```html
<script>
    //初始化时开始
    $(function () {
        countDown();
    });

    function countDown() {//倒计时
        var remainSeconds=$("#remainSeconds").val();//
        var timeout;
        if(remainSeconds>0){//秒杀未开始
            $("#buyButton").attr("disabled",true);//未开始按钮不可以点
            timeout = setTimeout(function () {
                $("#countDown").text(remainSeconds -1);//倒计时减一
                $("#remainSeconds").val(remainSeconds-1);
                countDown();
            },1000);//1秒后改掉当前时间
        }else if(remainSeconds == 0){//秒杀进行中
            $("#buyButton").attr("disabled",false);//秒杀开始，可以点按钮
            if(timeout){
                clearTimeout(timeout);
            }
            $("#seckillTip").html("秒杀进行中");
        }else {//秒杀已结束
            $("#buyButton").attr("disabled",true);//不能点
            $("#seckillTip").html("秒杀已结束");
        }
    }
</script>
```

对应controller层

```java
package com.seckill.controller;

import com.seckill.pojo.SeckillUser;
import com.seckill.redis.RedisService;
import com.seckill.service.GoodsService;
import com.seckill.service.SeckillUserService;
import com.seckill.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    public static final String COOKIE1_NAME_TOKEN="token";

    @Autowired
    RedisService redisService;
    @Autowired
    SeckillUserService seckillUserService;
    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")//跳转秒杀商品列表
    public String toList(Model model, SeckillUser user){
        /*//通过取到cookie，首先取@RequestParam没有再去取@CookieValue
        if( StringUtils.isEmpty(paramToken) && StringUtils.isEmpty(cookieToken)){
            return "login";
        }
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        SeckillUser user=seckillUserService.getByToken(token,response);*/
        model.addAttribute("user",user);
        //查询商品列表
        List<GoodsVo> list = goodsService.listGoodsVo();
        model.addAttribute("goodsList",list);
        return "goods_list";
    }
    //测试使用
    @RequestMapping("/to_list_test")
    public String list_test(Model model,@CookieValue(value = SeckillUserService.COOKIE1_NAME_TOKEN)String cookieToken,
                            @RequestParam(value = SeckillUserService.COOKIE1_NAME_TOKEN)String paramToken, HttpServletResponse response
            ,SeckillUser user){
        model.addAttribute("user",user);
        return "goods_list";
    }
    
    @RequestMapping("/to_detail/{goodsId}")//商品详情
    public String todetail(Model model, SeckillUser user,
                           @PathVariable("goodsId")long goodsId){
        //snowflake算法生成唯一id
        model.addAttribute("user",user);
        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);

        //
        int seckillStatus=0;
        int remainSeconds=0;//秒杀倒计时 start-now = remaintime  还剩多长时间
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        if(now<startAt){
            seckillStatus=0;//秒杀未开始
            remainSeconds=(int)((startAt-now)/100);
        }else if (now>endAt){
            seckillStatus=2;//秒杀已结束
            remainSeconds=-1;
        }else {
            seckillStatus=1;//秒杀进行中
            remainSeconds=0;
        }
        model.addAttribute("seckillStatus",seckillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        return "goods_detail";
    }
}

```

## 秒杀功能

单独从商品表拉出一张表作为秒杀商品表，原因就是便于维护。

秒杀时，除了生成订单信息，还单独生成一张秒杀订单，用于判断用户是否重复秒杀（根据秒杀订单表中的用户id，一般时存用户手机号，和商品id去查）



逻辑：

先判断库存，先把库存查出来判断是否为大于0。

小于0就返回失败。把失败信息扔到model里传给前端。

==防止重复秒杀==，我们需要查询秒杀订单表通过user.id,goods.id去查询。并且返回orderInfo对象。如果不为空，说明用户已经秒杀成功。返回不可重复秒杀信息给前端。

如果库存大于0，则开始秒杀。

1.减库存（秒杀商品表）

2.下订单（生成详细订单信息）

3.写入秒杀订单

这三个操作都是在service层使用事务@Transactional操作（原子操作）。

```
keyProperty = "id"
```

指的是数据库中的id对应实体类中id字段

第一次压测。

![1605850441281](C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605850441281.png)

/goods/to_list

请求10000次，发现qps最高950，查看虚拟机运行，发现这是mysql的最大瓶颈。所以需要去优化。系统负载load达到15.

<img src="C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605846467939.png" alt="1605846467939" style="zoom:50%;" />

![1605847727137](C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605847727137.png)

查询用户信息/User/info

并且携带token。token是写到Cookie中的。查询的时候是查redis。

使用redis时QPS达到2000多。缓存还是很强大的。（这里使用的是同一个token）

<img src="C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605850253239.png" alt="1605850253239" style="zoom:80%;" />

模拟不同的token进行压测。

1.添加CSV配置文件

<img src="C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605850673171.png" alt="1605850673171" style="zoom:67%;" />

2.编写config.txt文件

编写变量名称

<img src="C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605851031927.png" alt="1605851031927" style="zoom:50%;" />

![1605850970447](C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605850970447.png)

压测结果达到每秒2000

<img src="C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605851218283.png" alt="1605851218283" style="zoom:80%;" />

开始正式测试秒杀接口

先动态生成1000个用户，然后jmeter启动5000进程，循环10次就是五万个请求，压测秒杀接口。

<img src="C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1606008941886.png" alt="1606008941886" style="zoom:50%;" />

可以看到QBS达到600左右。

但是却发生了线程超卖问题，所以还需要解决此问题。

超卖原因，两个线程A，B同时进入，A判断库存大于0，B判断也是大于0

则都可以往下执行，但是他们两都没秒杀到

<img src="C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1606009025170.png" alt="1606009025170" style="zoom:67%;" />



![1605920760940](C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605920760940.png)

使用redis-benchmark对其进行压测。

### 第五章

页面优化技术

<img src="C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605929285222.png" alt="1605929285222" style="zoom:50%;" />

秒杀瓶颈在于mysql数据库的瓶颈

所以我们需要利用缓存来优化。

### 页面缓存

页面静态化，通过js、ajax去请求服务端拉到数据，然后渲染页面。浏览器将html页面缓存到客户端，不需要重复去请求服务端减少服务器压力。

页面缓存时间不宜过长设置一分钟就行，过长数据的及时性就不高。

页面的缓存就是把整个页面存到redis中，ajax取到数据，然后通过手动渲染页面。这样用户访问就可以直接先访问缓存中的页面

### URL级缓存

带有不同参数，页面详情不同，叫做url缓存。

### 对象缓存

粒度最小，

流程先查缓存，缓存没有查数据库，数据库中的再加入到缓存，然后再返回。

如果有数据更新，不如更新密码，一定要把缓存也更新。

```java
 public SeckillUser getById(long id){
        SeckillUser user = redisService.get(SeckillUserKey.getById, "" + id, SeckillUser.class);
       //先去缓存查数据
        if(user!=null){
            return user;
        }
        //没有就到数据库查
        user = seckillUserDao.getById(id);
        //设置缓存
        if(user!=null){
            redisService.set(SeckillUserKey.getById,""+id,user);
        }
        return user;
    }
    //更新密码
    public boolean updatePassword(String token,long id,String formPass){
        SeckillUser user = getById(id);
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOTEXIST);
        }
        //更新数据库
        SeckillUser update=new SeckillUser();
        update.setId(id);
        update.setPwd(MD5Util.formPassToDBPass(formPass,user.getSalt()));
        seckillUserDao.update(update);
        //修改缓存
        redisService.delete(SeckillUserKey.getById,""+id);
        user.setPwd(update.getPwd());
        redisService.set(SeckillUserKey.token,token,user);
        return true;
    }
```



### 压测

![1605943305984](C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605943305984.png)

没做页面缓存之前QPS：950，做了之后提升到2000的QPS.可以看出优化的提高还是很大的。系统负载load在5左右。

### 商品详情静态化

<img src="C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1605950143813.png" alt="1605950143813" style="zoom:50%;" />

作用：把页面缓存到用户的浏览器上，不需要与服务器进行交互，从本地拿缓存，减少网络流量。

 **将页面直接缓存到用户的浏览器上面，好处：用户访问数据的时候，不用去请求服务器，直接在本地缓存中取得需要的页面缓存。** 

如果不做页面静态化，用户访问页面时，就需要去先查看缓存里有没有页面缓存，如果没有就需要去查询数据库，然后渲染页面，存到缓存中，再将整个html页面返回给客户端显示。

做了静态化，我们第一次请求后台时要去渲染页面，之后的请求都是直接访问用户本地浏览器的缓存里的html页面，静态资源，然后前端通过ajax来和后端交互，只需要获取需要显示的数据返回就可以。

控制静态资源缓存的时间，可以在properties里面设置。

goods_detail的静态化。

对秒杀页面进行静态化，通过对spring的配置文件中静态文件的配置，将页面缓存到用户的浏览器上，这样访问的时候就不需要去与数据库交互，减轻数据库的负担。

order_detail静态化。

### ==解决超卖问题==

![1607825518292](C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1607825518292.png)

```sql
update seckill_goods set stock_count = stock_count-1 where goods_id=#{goodsId} and stock_count>0
```

<u>当库存还有1的时候，两个进程同时进入，就可能导致超卖，只需要加上一个判断库存是否大于0，给数据库加个锁。并且在下订单时还需要去判断下减库存的操作是否成功，如果失败那么就不下单。这样订单也不会出现多下的现象。</u>

![1607243149077](C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1607243149077.png)

<u>**但是仅仅这样还不够，可能会出现一个人抢到两个商品的情况，试想如果库存现在是10，一个用户同时发送两个请求，它都会去减库存下订单，导致一个用户抢到2个商品。解决方法：给秒杀订单表加上一个唯一索引。**</u>

![1607224389040](C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1607224389040.png)

没有优化之前的压测

![1607243594459](C:\Users\svv\AppData\Roaming\Typora\typora-user-images\1607243594459.png)

qps很低，大概在490左右，此时系统的负载还是很高的大概在10左右。





### RabbitMq集成学习

简单的发送者，接收者以及MQConfig配置。

交换机Direct模式

```
处理路由键。
需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。这是一个完整的匹配。如果一个队列绑定到该交换机上要求路由键 “dog”，则只有被标记为“dog”的消息才被转发，不会转发 dog.puppy，也不会转发dog.guard，只会转发 dog。
```

Topic模式交换机

```
绑定键binding key也必须是这种形式。以特定路由键发送的消息将会发送到所有绑定键与之匹配的队列中。但绑定键有两种特殊的情况：
①*（星号）仅代表一个单词
②#（井号）代表任意个单词
我们就拿上面的图解释，*.orange.*能匹配 a.orange.a,b.orange.a，aa.orange.bb等等
lay.#能匹配的就多了，他只要一lay.开头的都匹配，他可以匹配lay.a,lay.a.b,lay.b.c等。
这样是不是很方便，比如我们想将log的发给q1队列，其他的发给q2，那么我们只需要定义log.#、或者log.*,那么你发送给q1队列的数据就是log日志的消息。
所以这种方式对于处理一个分类的消息特别方便。 
```

fanout模式交换机

```
（就是广播，所有队列都可以接收到消息），不处理路由键。你只需要将队列绑定到交换机上。发送消息到交换机都会被转发到与该交换机绑定的所有队列
```

header模式交换机。

### ==秒杀优化==

1.将库存加载到redis中。

2.Redis预减库存（只要redis库存光了，直接返回失败），

3.请求入队，并且立即返回排队中

每次入队之前都会去检查库存（查看redis的库存）是否大于0，如果小于0直接返回失败，所以入队的消息，也就是进入的用户会比较少。所以出队时操作mysql数据库，也就毫无压力了。

入队时，使用redis生成（set）一个SeckillMessage（秒杀消息 由user+goodsId组成的对象）

4.请求出队，生成订单，减少库存（访问数据库，这边入队的很少）

5.客户端进行轮询，查看秒杀结果。

```
 	 * 客户端做一个轮询，查看是否成功与失败，失败了则不用	   继续轮询。
     * 秒杀成功，返回订单的Id。
     * 库存不足直接返回-1。
     * 排队中则返回0。
     * 查看是否生成秒杀订单。
```

