package com.example.dangjian_spring.Controller;

import cn.hutool.core.util.StrUtil;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Role;
import com.example.dangjian_spring.service.RoleService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
public class RoleController  {

    @Resource
    private RoleService roleService;

    @GetMapping("/role")
    public Result getRoles() {
        List<Role> roles = roleService.listAllRoles();
        return Result.success(roles);
    }

    @PostMapping("/role/add")
    public Result addRole(@RequestBody Role role) {
        if(roleService.getRoleByName(role.getRole_name()) != null)
        {
            return Result.error("角色" + role.getRole_name() + "已存在");
        }
        else
        {
            int id = roleService.listAllRoles().size() + 1;
            role.setRole_id(id);
            if(roleService.addRole(role) != 0)
            {
                return getRoles();
            }
            else
            {
                return Result.error("添加失败");
            }
        }
    }

    @GetMapping("/role/delete/{role_id}")
    public Result delRole(@PathVariable int role_id) {
        if(roleService.deleteRoleById(role_id) != 0)
        {
            return Result.success("成功删除");
        }
        else
        {
            return Result.error("删除成败");
        }
    }

    @GetMapping("/role/search/{keyword}")
    public Result searchRole(@PathVariable String keyword) {
        keyword = "%" + keyword + "%";
        List<Role> roles = roleService.getRolesByKeyword(keyword);
        return Result.success(roles);
    }

    @PostMapping("/role/update")
    public Result updateRole(@RequestBody Role role) {
        if(roleService.getRoleById(role.getRole_id()) == null)
        {
            return Result.error("不存在该角色");
        }
        else
        {
            if(roleService.updateRole(role) != 0)
            {
                return getRoles();
            }
            else
            {
                return Result.error("修改失败");
            }
        }
    }

}

