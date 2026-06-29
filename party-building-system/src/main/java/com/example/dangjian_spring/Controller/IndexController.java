package com.example.dangjian_spring.Controller;

import cn.hutool.core.util.StrUtil;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.User2;
import com.example.dangjian_spring.service.User2Service;
import com.example.dangjian_spring.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
public class IndexController {
    @Resource
    User2Service user2Service;

    @GetMapping("/index")
    public Result index() {
        return Result.success("这里是主页");
    }

    @PostMapping("/log_in")
    public Result logIn(@RequestBody User2 user) {
        if(StrUtil.isBlank(user.getPassword()) || StrUtil.isBlank(user.getUser_name()))
        {
            return Result.error("密码或者用户名格式不正确");
        }
        else
        {
            User2 db_user = user2Service.getUserByName(user.getUser_name());
            if(db_user == null)
            {
                return Result.error("用户" + user.getUser_name() + "不存在");
            }
            else if(db_user.getPassword().equals(user.getPassword()))
            {
                String token = TokenUtils.createToken(db_user.getUser_id().toString(), db_user.getPassword());
                db_user.setToken(token);
                return Result.success(db_user);
            }
            else
            {
                return Result.error("密码" + user.getPassword() + "不正确");
            }

        }
    }

    @PostMapping("/sign_in")
    public Result signIn(@RequestBody User2 user) {
        if(StrUtil.isBlank(user.getPassword()) || StrUtil.isBlank(user.getUser_name()))
        {
            return Result.error("密码或者用户名格式不正确");
        }
        else
        {
            User2 db_user = user2Service.getUserByName(user.getUser_name());
            if(db_user != null)
            {
                return Result.error("用户" + db_user.getUser_name() + "已存在");
            }
            else
            {
                user.setUser_id(user2Service.nextUserIdForRegister());
                if(user2Service.addUser(user) != 0)
                {
                    return Result.success("注册成功");
                }
                else
                {
                    return Result.error("注册失败");
                }
            }
        }
    }
}
