package com.dy.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dy.common.R;
import com.dy.entity.Employee;
import com.dy.entity.User;

import com.dy.exception.CustomException;
import com.dy.service.EmployeeService;
import com.dy.service.UserService;

import com.dy.utils.EmpThreadLocal;
import com.dy.utils.JwtUtils;
import com.dy.utils.RedisUtils;
import com.dy.utils.UserThreadLocal;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

//@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private EmployeeService employeeService;
    @Resource
    private RedisUtils redisUtils;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;


    private List<String> urls = new ArrayList<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        System.out.println("token---->" + token);
        if (handler instanceof HandlerMethod) {

            if (StringUtils.isEmpty(token)) {
                response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
                return false;
                //throw new CustomException("签名验证不存在!");


            } else {
                Claims claims = JwtUtils.validateJWT(token).getClaims();
                if (claims == null) {
                    response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
                    return false;
                    //throw new CustomException("鉴权失败!");

                } else {
                    String id = JwtUtils.parseJWT(token).getId();
                    System.out.println(id+"id");
                    User byId = this.userService.getById(id);
                    Employee employee = this.employeeService.getById(id);
                    if (byId!=null){
                        UserThreadLocal.put(byId);
                        return true;
                    }
                    if (employee !=null){
                        EmpThreadLocal.put(employee);
                        return true;
                    }
                    return false;

                }
            }
        } else {
            return true;
        }
    }
    public List<String> getUrls(){

        urls.add("/employee/login");
        urls.add("/employee/logout");
        urls.add("/backend/**");
        urls.add("/front/**");
        urls.add("/user/sendMsg");
        urls.add("/user/login");
        urls.add("/user/wxLogin");


        return urls;
    }
}
