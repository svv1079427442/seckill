package com.seckill.controller;

import com.github.pagehelper.PageException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seckill.pojo.*;
import com.seckill.result.Result;
import com.seckill.service.AdminService;
import com.seckill.service.GoodsService;

import com.seckill.vo.SeckillGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    GoodsService goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    AdminService adminService;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping("/home")
    @ResponseBody
    public String Home(Admin admin, Model model, HttpServletRequest request,HttpServletResponse response) {
        HttpSession session = request.getSession();
        Object admin1 = session.getAttribute("admin");
        System.out.println("管理员信息————："+admin1);
        model.addAttribute("name",admin1);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("index", springWebContext);
        return html;
    }

    /**
     * 后台首页
     *
     * @param admin
     * @param model
     * @return
     */
    @RequestMapping("/welcome")
    public String toWelcome(Admin admin, Model model) {
        return "welcome";

    }

    /**
     * 商品详情页面
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/goods_detail", produces = "text/html")
    @ResponseBody
    public String toGood_detail(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") int pageNum, @RequestParam(defaultValue = "5", value = "pageSize") int pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        List<Goods> goodsList = goodsService.getList();
        PageInfo<Goods> goodsPageInfo = new PageInfo<>(goodsList);
        System.out.println("当前页"+pageNum);
        System.out.println("每页条数"+pageSize);
        System.out.println("一共有：" + goodsPageInfo.getPages() + "页");
        List<Goods> list = goodsPageInfo.getList();
        System.out.println("**商品列表**：" + list);
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", goodsPageInfo);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("goods_list_back", springWebContext);
        return html;
    }
    /**
     * 商品添加页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/add", produces = "text/html")
    @ResponseBody
    public String to_good_add(HttpServletRequest request, HttpServletResponse response, Model model) {
        String id = request.getParameter("id");
        System.out.println("获取编号id为：" + id);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("add", springWebContext);
        return html;
    }
    /**
     * 商品详情页面
     *
     * @param model
     * @return
     */
    @PostMapping(value = "/good_add", produces = "text/html")
    @ResponseBody
    public String good_add(HttpServletRequest request, HttpServletResponse response, Model model) {
        System.out.println("++++商品添加++++");
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("add", springWebContext);
        return html;
    }


    /**
     * 添加秒杀商品
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/sec_good_add", produces = "text/html")
    @ResponseBody
    public String sec_good_add(HttpServletRequest request, HttpServletResponse response, Model model) {
        System.out.println("++++秒杀商品添加++++");
        List<Goods> list = goodsService.getGoodsList();
        model.addAttribute("goodsList",list);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("add_sec_good", springWebContext);
        return html;
    }
    /**
     * 添加秒杀商品
     *
     * @param model
     * @return
     */
    @PostMapping(value = "/sec_good_adds", produces = "text/html")
    @ResponseBody
    public String sec_good_adds(HttpServletRequest request, HttpServletResponse response, Model model) {
        System.out.println("++++秒杀商品添加++++");
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("add_sec_good", springWebContext);
        return html;
    }
    /**
     * 秒杀商品详情页面
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/seckill_goods", produces = "text/html")
    @ResponseBody
    public String toSeckillGoods(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "5", value = "pageSize") Integer pageSize) {
        PageInfo<SeckillGoods> pageInfo = goodsService.getSeckillGoods(pageNum, pageSize);
        System.out.println("一共有：" + pageInfo.getPages() + "页");
        List<SeckillGoods> list = pageInfo.getList();
        List<SeckillGoodsVo> goods = adminService.getNameById();

        System.out.println("**商品列表**：" + list);
        model.addAttribute("goodsList",goods);
        model.addAttribute("pageInfo", pageInfo);
        //return "goods_list";
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("seckill_goods", springWebContext);
        return html;
    }

    /**
     * 秒杀商品查询
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/sec_goods_search", produces = "text/html")
    @ResponseBody
    public String seckillgoods_search(HttpServletRequest request, HttpServletResponse response, Model model) {
        String name = request.getParameter("name");
        List<SeckillGoodsVo> seckillGoodsVos = adminService.search_res(name);
        model.addAttribute("goodsList", seckillGoodsVos);
        return "sec_goods_search";
    }
    @GetMapping(value = "/sec_goods_searchs", produces = "text/html")
    public String seckillgoods_searchs(HttpServletRequest request, HttpServletResponse response, Model model) {
        String name = request.getParameter("name");
        System.out.println("******名字是："+name);
        List<SeckillGoodsVo> seckillGoodsVos = adminService.search_res(name);
        System.out.println("查询："+seckillGoodsVos.toString());
        model.addAttribute("goodsList", seckillGoodsVos);
        return "sec_goods_search";
    }
    /**
     * 订单详情页面
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/order_details", produces = "text/html")
    @ResponseBody
    public String toOrderDetail(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "5", value = "pageSize") Integer pageSize) {

        PageInfo<OrderInfo> pageInfo = goodsService.getOrderDetail(pageNum, pageSize);
        System.out.println("一共有：" + pageInfo.getPages() + "页");
        List<OrderInfo> list = pageInfo.getList();
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", pageInfo);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("order_details", springWebContext);
        return html;
    }
    /**
     * 秒杀订单详情页面
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/seckill_order", produces = "text/html")
    @ResponseBody
    public String toSeckillOrder(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "5", value = "pageSize") Integer pageSize) {
        PageInfo<SeckillOrder> pageInfo = goodsService.getSeckillOrder(pageNum, pageSize);
        System.out.println("一共有：" + pageInfo.getPages() + "页");
        List<SeckillOrder> list = pageInfo.getList();
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", pageInfo);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("seckill_order", springWebContext);
        return html;
    }
    /**
     * 用户列表页面
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/seckill_user", produces = "text/html")
    @ResponseBody
    public String toUserList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "5", value = "pageSize") Integer pageSize) {
        PageInfo<SeckillUser> pageInfo = goodsService.getSeckillUser(pageNum, pageSize);
        System.out.println("一共有：" + pageInfo.getPages() + "页");
        List<SeckillUser> list = pageInfo.getList();
       // System.out.println(list.toArray());
        Object[] array = list.toArray();
        System.out.println(array[1]);
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", pageInfo);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("seckill_user", springWebContext);
        return html;
    }

    /**
     * 用户列表查询
     * @param request
     * @param response
     * @param model
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/userList_search", produces = "text/html")
    @ResponseBody
    public String user_search(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "5", value = "pageSize") Integer pageSize) {
        String mobile = request.getParameter("mobile");
        long id = Long.valueOf(mobile);
        PageInfo<SeckillUser> pageInfo = goodsService.getSeckillUserByIdOrUsername(pageNum, pageSize,id);
        System.out.println("一共有：" + pageInfo.getPages() + "页");
        List<SeckillUser> list = pageInfo.getList();
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", pageInfo);
        return "seckill_user_search";
    }
    /**
     * 用户列表查询结果
     * @param request
     * @param response
     * @param model
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/userList_search_res")
    public String user_search_res(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "5", value = "pageSize") Integer pageSize) {
        String mobile = request.getParameter("mobile");
        long id = Long.valueOf(mobile);
        PageInfo<SeckillUser> pageInfo = goodsService.getSeckillUserByIdOrUsername(pageNum, pageSize,id);
        System.out.println("一共有：" + pageInfo.getPages() + "页");
        List<SeckillUser> list = pageInfo.getList();
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", pageInfo);
        return "seckill_user_search";
    }

    /**
     * 删除商品
     * @param request
     * @param response
     * @param model
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/del", produces = "text/html")
    @ResponseBody
    public void del(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        goodsService.del_goods(id);
        response.getWriter().write("true");
    }

    /**
     * 删除用户
     * @param request
     * @param response
     * @param model
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/del_user", produces = "text/html")
    @ResponseBody
    public void del_user(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        long id = Long.valueOf(request.getParameter("id"));
        goodsService.del_User(id);
        response.getWriter().write("1");
    }
    /**
     * 删除秒杀商品
     * @param request
     * @param response
     * @param model
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/del_goods", produces = "text/html")
    @ResponseBody
    public void del_goods(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        long id = Long.valueOf(request.getParameter("id"));
        System.out.println("删除删除删除");
        goodsService.delSeckillGoods(id);
        response.getWriter().write("1");
    }
    @GetMapping(value = "/update/{id}", produces = "text/html")
    @ResponseBody
    public String to_update(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String id) throws IOException {
        System.out.println("获取编号id为：" + id);
        int ids = Integer.parseInt(id);
        Goods list = goodsService.getById(ids);
        model.addAttribute("goodsList", list);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("update", springWebContext);
        return html;
    }

    /**
     * 商品列表_复现修改内容
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/update", produces = "text/html")
    @ResponseBody
    public String fuxian_update(HttpServletRequest request, HttpServletResponse response, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        System.out.println("获取编号id为：" + id);
        Goods list = goodsService.getById(id);
        model.addAttribute("goodsList", list);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("update", springWebContext);
        return html;
    }
    /**
     * 用户列表_复现修改内容
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/update_user", produces = "text/html")
    @ResponseBody
    public String fuxian_update_user(HttpServletRequest request, HttpServletResponse response, Model model) {
        long id = Long.valueOf(request.getParameter("id"));
        System.out.println("获取编号id为：" + id);
        SeckillUser list = goodsService.getUserById(id);
        model.addAttribute("userList", list);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("update_user", springWebContext);
        return html;
    }
    /**
     * 用户列表_复现修改内容
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/update_seckill_goods", produces = "text/html")
    @ResponseBody
    public String fuxian_update_seckill_goods(HttpServletRequest request, HttpServletResponse response, Model model) {
        long id = Long.valueOf(request.getParameter("id"));
        System.out.println("获取编号id为：" + id);
        SeckillGoodsVo list = goodsService.getSecGoodsById(id);
        list.setGoodsName(goodsService.getNameById(id));
        model.addAttribute("goodsList", list);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("update_seckill_goods", springWebContext);
        return html;
    }

    /**
     * 修改提交_商品列表
     */
    @RequestMapping(value = "/update_submit", produces = "text/html")
    @ResponseBody
    public Result<Integer> submit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("goods_id"));
        System.out.println("获取的id为：" + id);
        String goods_name = request.getParameter("goods_name");
        System.out.println("获取的goods_name为：" + goods_name);
        String goods_title = request.getParameter("goods_title");
        System.out.println("获取的goods_title为：" + goods_title);
        BigDecimal goods_price = new BigDecimal(request.getParameter("goods_price"));
        System.out.println("获取的goods_price为：" + goods_price);
        int goods_stock = Integer.parseInt(request.getParameter("goods_stock"));
        System.out.println("获取的goods_stock为：" + goods_stock);
        int result = goodsService.update(id, goods_name, goods_title, goods_price, goods_stock);
        return Result.success(result);
    }
    /**
     * 修改提交_用户
     */
    @RequestMapping(value = "/submit_user", produces = "text/html")
    @ResponseBody
    public void submit_user(HttpServletRequest request, HttpServletResponse response) throws IOException{
        long id = Long.valueOf(request.getParameter("user_id"));
        String nickname = request.getParameter("user_name");
        String address = request.getParameter("address");
        int result = goodsService.update_user(id, nickname, address);
        response.getWriter().write("1");
    }
    /**
     * 添加提交
     */
    @RequestMapping(value = "/add_submit", produces = "text/html")
    public void add_submit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String goods_name = request.getParameter("goods_name");
        String goods_price = request.getParameter("goods_price");
        String image = request.getParameter("goods_image");
        String goods_title = request.getParameter("goods_detail");
        String seckill_count = request.getParameter("seckill_count");
        System.out.println("进入++++++++++++++++++++++++");
        String substring = image.substring(12, image.length());
        String goods_image="/img/"+substring;
        BigDecimal price=new BigDecimal(goods_price);
        goodsService.addGood(goods_name,goods_title,price,Integer.valueOf(seckill_count),goods_image);
        response.getWriter().write("1");
    }
    /**
     * 添加提交
     */
    @RequestMapping(value = "/add_submit_sec_good", produces = "text/html")
    public void add_submit_sec_good(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String goods_name = request.getParameter("goods_name");
        String goods_id = request.getParameter("goods_id");
        System.out.println("名字:"+goods_name);
        System.out.println("id:"+goods_id);
        String seckill_price = request.getParameter("seckill_price");
        String count = request.getParameter("seckill_count");
        String start_time = request.getParameter("start_time");
        String end_time = request.getParameter("end_time");
        try {
            Date start = simpleDateFormat.parse(start_time);
            Date end = simpleDateFormat.parse(end_time);
            goodsService.add_sec_goods(Integer.parseInt(goods_id),Integer.parseInt(count),new BigDecimal(seckill_price),start,end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        response.getWriter().write("1");
    }
    /**
     * 搜索
     */
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    @ResponseBody
    public String serach(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") int pageNum, @RequestParam(defaultValue = "3", value = "pageSize") int pageSize) throws ServletException, IOException {
        String goods_name = request.getParameter("goods_name");
        System.out.println("获取到的商品名11："+goods_name);
        PageHelper.startPage(pageNum,pageSize);
        List<Goods> goodsList = goodsService.getByGoodsName(goods_name);

        System.out.println("结果集："+goodsList.toString());
        for (Goods list:goodsList) {
            System.out.println(list.toString());
        }
        PageInfo<Goods> goodsPageInfo = new PageInfo<>(goodsList);
        System.out.println("当前页"+pageNum);
        System.out.println("每页条数"+pageSize);
        System.out.println("一共有：" + goodsPageInfo.getPages() + "页");
        List<Goods> list = goodsPageInfo.getList();
        System.out.println("**商品列表**：" + list);
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", goodsPageInfo);
        return "goods_list_back_search";
    }
    /**
     * 搜索提交
     */
    @RequestMapping(value = "/search_res",method = RequestMethod.GET)

    public String serach_res(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") int pageNum, @RequestParam(defaultValue = "3", value = "pageSize") int pageSize) throws ServletException, IOException {
        String goods_name = request.getParameter("goods_name");
        System.out.println("获取到的商品名11："+goods_name);
        PageHelper.startPage(pageNum,pageSize);
        List<Goods> goodsList = goodsService.getByGoodsName(goods_name);
        System.out.println("结果集："+goodsList.toString());
        for (Goods list:goodsList) {
            System.out.println(list.toString());
        }
        PageInfo<Goods> goodsPageInfo = new PageInfo<>(goodsList);
        System.out.println("当前页"+pageNum);
        System.out.println("每页条数"+pageSize);
        System.out.println("一共有：" + goodsPageInfo.getPages() + "页");
        List<Goods> list = goodsPageInfo.getList();
        System.out.println("**商品列表**：" + list);
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", goodsPageInfo);
        return "goods_list_back_search";
    }
    /**
     * 管理员个人中心
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/admin_set", produces = "text/html")
    @ResponseBody
    public String admin_set(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") int pageNum, @RequestParam(defaultValue = "5", value = "pageSize") int pageSize) {
        HttpSession session = request.getSession();
        Object admin1 = session.getAttribute("admin");
        String name = admin1.toString();
        Admin admin = goodsService.getByAdName(name);
        model.addAttribute("adminInfo",admin);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("admin_set", springWebContext);
        return html;
    }

    /**
     * 管理员列表
     * @param request
     * @param response
     * @param model
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/admin_list", produces = "text/html")
    @ResponseBody
    public String admin_list(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") int pageNum, @RequestParam(defaultValue = "5", value = "pageSize") int pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        List<Admin> goodsList = goodsService.getAdminList();
        PageInfo<Admin> goodsPageInfo = new PageInfo<>(goodsList);
        System.out.println("当前页"+pageNum);
        System.out.println("每页条数"+pageSize);
        System.out.println("一共有：" + goodsPageInfo.getPages() + "页");
        List<Admin> list = goodsPageInfo.getList();
        System.out.println("**管理员列表**：" + list);
        model.addAttribute("adminList", list);
        model.addAttribute("pageInfo", goodsPageInfo);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("admin_list", springWebContext);
        return html;
    }
}
