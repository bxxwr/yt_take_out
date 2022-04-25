package com.dy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dy.common.R;
import com.dy.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {

    /**
     * 登录
     * @param employee
     * @param request
     * @return
     */
    R<Employee> login(Employee employee, HttpServletRequest request);

    /**
     * 退出
     * @param request
     * @return
     */
    R<String> logout(HttpServletRequest request);

    /**
     * 分页查询
     * @param pageInfo
     * @param name
     * @return
     */
    R<Page> pageQuery(Page pageInfo, String name);



}
