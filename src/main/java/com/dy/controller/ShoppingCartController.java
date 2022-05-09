package com.dy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dy.common.R;
import com.dy.entity.ShoppingCart;
import com.dy.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping ("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        return shoppingCartService.add2shoppingCart(shoppingCart);
    }


    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,request.getSession().getAttribute("user"));
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,request.getSession().getAttribute("user"));
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功！");
    }

    @PostMapping("/sub")
    public R<ShoppingCart> changeNumber(@RequestBody ShoppingCart shoppingCart){
        Long userId = (Long) request.getSession().getAttribute("user");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(shoppingCart.getDishId() != null,ShoppingCart::getDishId,shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getSetmealId() != null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one.getNumber() == 1){
            shoppingCartService.removeById(one);
        }
        Integer number = one.getNumber();
        one.setNumber(number-1);
        shoppingCartService.updateById(one);
        return R.success(one);

    }
}
