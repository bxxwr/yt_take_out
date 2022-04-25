package com.dy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dy.common.R;
import com.dy.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    R<User> login(Map map);
}
