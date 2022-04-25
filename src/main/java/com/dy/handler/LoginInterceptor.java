package com.dy.handler;

import com.alibaba.fastjson.JSON;
import com.dy.common.R;
import com.dy.entity.Employee;
import com.dy.service.EmployeeService;
import com.dy.utils.EmpThreadLocal;
import com.dy.utils.RedisUtils;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
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

    private List<String> urls = new ArrayList<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        //if (session.getAttribute("user") != null && redisUtils.getUserFromRedis(session.getAttribute("user").toString()).equals(session.getAttribute("user"))){
        //    log.info("用户已登录，用户ID为:{}",request.getSession().getAttribute("user"));
        //    return true;
        //}else if (session.getAttribute("employee") != null && redisUtils.getUserFromRedis(session.getAttribute("employee").toString()).equals(session.getAttribute("user"))){
        //    log.info("用户已登录，用户ID为:{}",request.getSession().getAttribute("employee"));
        //    return true;
        //}else{
        //    response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        //    return false;
        //}




        //if (session.getAttribute("employee") != null || (session.getAttribute("user") != null)){
        //    if (session.getAttribute("employee").equals("employee")){
        //        log.info("用户已登录，用户ID为:{}",request.getSession().getAttribute("employee"));
        //
        //    }
        //    if (session.getAttribute("user").equals("user")){
        //        log.info("用户已登录，用户ID为:{}",request.getSession().getAttribute("user"));
        //    }
        //
        //    return true;
        //}else{
        //    response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        //    return false;
        //}
        if (session.getAttribute("employee") != null && redisUtils.isEmployeeInRedis(session.getAttribute("employee").toString())){
                log.info("用户已登录，用户ID为:{}",request.getSession().getAttribute("employee"));
                return true;
        }
        if (session.getAttribute("user") != null && redisUtils.isUserInRedis(session.getAttribute("user").toString())){
            log.info("用户已登录，用户ID为:{}",request.getSession().getAttribute("user"));
            return true;
        }
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return false;




    }



    public List<String> getUrls(){

        urls.add("/employee/login");
        urls.add("/employee/logout");
        urls.add("/backend/**");
        urls.add("/front/**");
        urls.add("/user/sendMsg");
        urls.add("/user/login");

        return urls;
    }
}
