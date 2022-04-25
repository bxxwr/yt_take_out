package com.dy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dy.common.R;
import com.dy.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    /**
     * 添加菜品或套餐进购物车
     * @param shoppingCart
     * @return
     */
    R<ShoppingCart> add2shoppingCart(ShoppingCart shoppingCart);
}
