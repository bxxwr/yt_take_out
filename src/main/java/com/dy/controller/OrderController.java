package com.dy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dy.common.R;
import com.dy.dto.OrdersDto;
import com.dy.entity.Orders;
import com.dy.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.XMLConstants;
import javax.xml.ws.soap.Addressing;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){

        Page pageInfo = new Page<>(page,pageSize);
        return orderService.pageQuery(pageInfo,number,beginTime,endTime);
    }
    @PutMapping
    public R<String> status(@RequestBody OrdersDto ordersDto){
        orderService.updateStatusById(ordersDto);
        return R.success("修改订单状态成功！");
    }


}
