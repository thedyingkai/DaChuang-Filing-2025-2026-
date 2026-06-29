package com.example.dangjian_spring.Controller;

import cn.hutool.core.util.StrUtil;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.User2;
import com.example.dangjian_spring.service.User2Service;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
public class User2Controller {
    @Resource
    User2Service user2Service;

    @GetMapping("/users")
    public Result getUsers() {
        List<User2> user_list = user2Service.listAll();
        return Result.success(user_list);
    }

    @GetMapping("/users/delete/{u_id}")
    public Result deleteUser(@PathVariable int u_id) {
        if(user2Service.getUserById(u_id) == null) {
            return Result.error("用户" + u_id + "不存在");
        }
        else
        {
            if(user2Service.deleteUser(u_id) != 0)
            {
                return Result.success("删除成功");
            }
            else
            {
                return Result.error("删除失败");
            }
        }
    }

    @PostMapping("/users/update")
    public Result updateUser(@RequestBody User2 user2) {
        if(user2Service.getUserById(user2.getUser_id()) == null)
        {
            return Result.error("用户" + user2.getUser_name() + "不存在");

        }
        else
        {
            if(user2Service.updateUser(user2) != 0)
            {
                return Result.success(user2Service.listAll());
            }
            else
            {
                return Result.error("更新失败");
            }
        }
    }

    @PostMapping("/users/add")
    public Result addUser(@RequestBody User2 user2) {
        if(user2Service.getUserByName(user2.getUser_name()) == null)
        {
            int id = user2Service.listAll().size() + 1;
            user2.setUser_id(id);
            if(user2Service.addUser(user2) != 0)
            {
                return Result.success(user2Service.listAll());
            }
            else
            {
                return Result.error("添加失败");
            }
        }
        else
        {
            return Result.error("用户名" + user2.getUser_name() + "已存在");
        }
    }

    @GetMapping("/users/search/{keyword}")
    public Result searchUsers(@PathVariable String keyword) {
        List<User2> users = user2Service.searchUsers(keyword);
        return Result.success(users);
    }



}
