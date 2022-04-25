package com.dy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.common.R;
import com.dy.entity.Employee;
import com.dy.mapper.EmployeeMapper;
import com.dy.service.EmployeeService;
import com.dy.utils.JWTUtils;
import com.dy.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private RedisUtils redisUtils;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private HttpServletRequest httpServletRequest;


    @Override
    public R<Employee> login(Employee employee,HttpServletRequest request) {

        //1.将页面提交的密码进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        //2.根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeMapper.selectOne(queryWrapper);

        //3.如果没有查到则返回登录失败结果
        if (emp == null){
            return R.error("登录失败！");
        }
        //4.密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败！");
        }
        //5.查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0){
            return R.error("账号已禁用！");
        }

        //if (!redisUtils.saveEmployee2Redis(emp.getId().toString())){
        //    return R.error("账号已登录");
        //}
        redisUtils.saveEmployee2Redis(emp.getId().toString());
        //6.登录成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());


        return R.success(emp);
    }

    @Override
    public R<String> logout(HttpServletRequest request) {
        Long empId = (Long)request.getSession().getAttribute("employee");
        redisUtils.deleteEmployeeFromRedis(empId.toString());
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    @Override
    public R<Page> pageQuery(Page pageInfo, String name) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeMapper.selectMapsPage(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @Override
    public boolean save(Employee employee) {
        //设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //
        //Long empId = (Long) httpServletRequest.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);
        employeeMapper.insert(employee);
        return true;
    }

    @Override
    public boolean updateById(Employee employee) {
        //Long empId = (Long) httpServletRequest.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        employeeMapper.updateById(employee);

        return true;
    }
}
