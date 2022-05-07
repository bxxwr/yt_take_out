package com.dy.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dy.common.R;
import com.dy.entity.Employee;
import com.dy.entity.User;
import com.dy.service.UserService;
import com.dy.utils.RedisUtils;
import com.dy.utils.SMSUtils;
import com.dy.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/loginout")
    public R<String> logout(){
        return userService.logout();
    }

    @GetMapping("/getUserInfo/{phone}")
    public R<User> getUserInfo(@PathVariable String phone){

        LambdaQueryWrapper<User> queryWrapper  = new LambdaQueryWrapper<>();
        queryWrapper.eq(phone != null , User::getPhone,phone);
        User byId = userService.getOne(queryWrapper);
        if (byId !=null){
            return R.success(byId);
        }
        return R.error("用户信息查询失败！");

    }
    @PutMapping
    public R<String> updateUser(@RequestBody User user){
        userService.updateUserById(user);
        return R.success("修改成功！");
    }

    @GetMapping("/getUserCount")
    public R<Long> getUserCount(){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        long count = userService.count(queryWrapper);
        return R.success(count);
    }

    /**
     * 会员分页条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //构造分页构造器
        Page pageInfo = new Page<>(page,pageSize);
        return userService.pageQuery(pageInfo,name);
    }

    /**
     * 根据ID修改会员信息
     * @param user
     * @return
     */
    @PutMapping("/updateStatus")
    public R<String> update(@RequestBody User user){
        userService.updateById(user);
        return R.success("会员信息修改成功！");
    }

    /**
     * 修改页面，根据ID回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id){
        User user = userService.getById(id);
        if (user != null){
            return R.success(user);
        }
        return R.error("没有查询到对应会员信息");
    }




}
