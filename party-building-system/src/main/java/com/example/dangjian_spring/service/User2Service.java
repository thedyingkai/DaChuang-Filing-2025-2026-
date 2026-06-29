package com.example.dangjian_spring.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.common.Page;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.dao.mapper.User2Mapper;
import com.example.dangjian_spring.entity.User2;
import com.example.dangjian_spring.exception.ServiceException;
import com.example.dangjian_spring.utils.TokenUtils;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class User2Service extends ServiceImpl<User2Mapper, User2> {

    @Resource
    private User2Mapper user2Mapper;

    public List<User2> listAll() {
        return user2Mapper.listAllUsers();
    }

    public User2 getUserById(int id) {
        return user2Mapper.selectById(id);
    }

    /** 注册专用：取 max(user1.uid)+1 与 max(User2.user_id)+1 的较大者，避免与党建 user1 主键冲突 */
    public int nextUserIdForRegister() {
        return user2Mapper.nextAvailableUser2Id();
    }

    public User2 getUserByName(String username) {
        return user2Mapper.selectUserByName(username);
    }

    public int addUser(User2 user2) {
        return user2Mapper.addUser(user2);
    }

    public int updateUser(User2 user2) {
        return user2Mapper.updateUserInfo(user2);
    }

    public int deleteUser(int id) {
        return user2Mapper.deleteById(id);
    }

    public List<User2> searchUsers(String keyword) {
        keyword = "%" + keyword + "%";
        return user2Mapper.searchUsersByKeyword(keyword);
    }


}
