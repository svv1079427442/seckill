package com.seckill.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seckill.pojo.*;
import com.seckill.service.GoodsService;

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
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    GoodsService goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    ApplicationContext applicationContext;

    @RequestMapping("/home")
    public String toHome(Admin admin, Model model) {
        return "index";
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
        System.out.println("**商品列表**：" + list);
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", pageInfo);
        //return "goods_list";
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("seckill_goods", springWebContext);
        return html;
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
    public String toUserList(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "7", value = "pageSize") Integer pageSize) {
        PageInfo<SeckillUser> pageInfo = goodsService.getSeckillUser(pageNum, pageSize);
        System.out.println("一共有：" + pageInfo.getPages() + "页");
        List<SeckillUser> list = pageInfo.getList();
        System.out.println(list.toString());
        model.addAttribute("goodsList", list);
        model.addAttribute("pageInfo", pageInfo);
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        //手动渲染
        String html = thymeleafViewResolver.getTemplateEngine().process("seckill_user", springWebContext);
        return html;
    }
    @RequestMapping(value = "/del", produces = "text/html")
    @ResponseBody
    public void del(HttpServletRequest request, HttpServletResponse response, Model model) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        goodsService.del_goods(id);
        response.getWriter().write("true");
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
     * 复现修改内容
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
     *
     */
    @RequestMapping(value = "/update_submit", produces = "text/html")
    @ResponseBody
    public void submit(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        response.getWriter().write("1");
    }
}
