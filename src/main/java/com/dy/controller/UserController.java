package com.dy.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dy.common.R;
import com.dy.entity.User;
import com.dy.service.UserService;
import com.dy.utils.RedisUtils;
import com.dy.utils.SMSUtils;
import com.dy.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private RedisUtils redisUtils;

    @Autowired
    private UserService userService;

    /**
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            //调用腾讯云Api完成发送短信
            log.info("code={}",code);
            //SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,phone,code);
            redisUtils.saveValidateCode2Redis(phone,code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");

    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map){

        return userService.login(map);
    }



}
