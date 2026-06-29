package com.example.dangjian_spring.Controller;

import cn.hutool.core.util.StrUtil;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.User;
import com.example.dangjian_spring.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;


@RestController
public class WebController {

    @Resource
    UserService userService;

    @GetMapping("/")
    public Result hello() {
        return Result.success("success");
    }

    @AuthAccess
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUname()) || StrUtil.isBlank(user.getPsw())) {
            return Result.error("数据输入不合法");
        }
        user = userService.login(user);
        return Result.success(user);
    }

    @AuthAccess
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        if (StrUtil.isBlank(user.getUname())||StrUtil.isBlank(user.getPsw())){
            return Result.error("数据输入不合法");
        }
        if (user.getUname().length()>10|| user.getPsw().length()>20){
            return Result.error("用户名或密码长度非法");
        }
        user =userService.register(user);
        return Result.success(user);
    }
}
