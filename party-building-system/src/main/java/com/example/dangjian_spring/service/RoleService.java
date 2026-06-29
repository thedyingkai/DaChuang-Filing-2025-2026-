package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.common.Page;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.dao.mapper.RoleMapper;
import com.example.dangjian_spring.entity.Role;
import com.example.dangjian_spring.exception.ServiceException;
import com.example.dangjian_spring.utils.TokenUtils;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role>{

    @Resource
    private RoleMapper roleMapper;

    public List<Role> listAllRoles() {
        return roleMapper.listAllRoles();
    }
    public int addRole(Role role) {
        return roleMapper.addRole(role);
    }

    public int updateRole(Role role) {
        return roleMapper.updateRole(role);
    }

    public Role getRoleById(int id) {
        return roleMapper.selectById(id);
    }

    public int deleteRoleById(int id) {
        return roleMapper.deleteRoleById(id);
    }

    public Role getRoleByName(String name) {
        return roleMapper.selectRoleByName(name);
    }

    public List<Role> getRolesByKeyword(String keyword) {
        return roleMapper.searchRolesByKeyword(keyword);
    }
}
