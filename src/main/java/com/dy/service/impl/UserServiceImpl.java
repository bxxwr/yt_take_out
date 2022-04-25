package com.dy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.common.R;
import com.dy.entity.User;
import com.dy.mapper.UserMapper;
import com.dy.service.UserService;
import com.dy.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private RedisUtils redisUtils;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Override
    public R<User> login(Map map) {
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Redis中获取验证码
        String codeFromRedis = redisUtils.getValidateCodeFromRedis(phone);
        if (codeFromRedis != null && codeFromRedis.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userMapper.selectOne(queryWrapper);
            if (user == null){
                //当前用户为新用户，自动完成注册
                user = new User();
                user.setPhone(phone);
                userMapper.insert(user);

            }
            //3、向客户端写入Cookie，内容为用户手机号
            //Cookie cookie = new Cookie("member_login_telephone",telephone);
            //cookie.setPath("/");
            //cookie.setMaxAge(60 * 60 * 24 * 30);
            ////将Cookie写入到客户端浏览器
            //response.addCookie(cookie);
            //
            ////4、将会员信息保存到Redis，使用手机号作为key，保存时长为30分钟
            //jedisPool.getResource().setex(telephone,60 * 30, JSON.toJSON(member).toString());

            request.getSession().setAttribute("user",user.getId());
            System.out.println("User——session"+request.getSession().getAttribute("user"));
            Cookie cookie = new Cookie("user_login_telephone",phone);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 *30);
            response.addCookie(cookie);

            //将会员信息保存在Redis，使用手机号作为key，保存时长为30分钟
            //if (!redisUtils.saveUser2Redis(user.getId().toString())){
            //    return R.error("账号已登录");
            //}
            redisUtils.saveUser2Redis(user.getId().toString());
            return R.success(user);
        }

        return R.error("登录失败！");
    }
}
