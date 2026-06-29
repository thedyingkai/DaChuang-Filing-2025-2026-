package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.common.Page;
import com.example.dangjian_spring.dao.mapper.User1Mapper;
import com.example.dangjian_spring.entity.User;
import com.example.dangjian_spring.exception.ServiceException;
import com.example.dangjian_spring.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class UserService extends ServiceImpl<User1Mapper, User> {

    @Resource
    User1Mapper user1Mapper;


    public void insertUser(User user) {
        this.user1Mapper.insert(user);
    }

    public void updateUser(User user) {
        this.user1Mapper.updateUser(user);
    }

    public void deleteUser(Integer uid) {
        this.user1Mapper.deleteUser(uid);
    }

    public void batchDeleteUser(List<Integer> ids) {
        for (Integer uid : ids) {
            user1Mapper.deleteUser(uid);
        }
    }

    public List<User> selectAll() {
        return user1Mapper.selectAll();
    }

    public User selectByUid(Integer uid) {
        return user1Mapper.selectByUid(uid);
    }

    public User selectByUname(String uname) {
        return user1Mapper.selectByUname(uname);
    }

    public User selectByMore(String uname, Integer cid) {
        return user1Mapper.selectByMore(uname, cid);
    }

    public List<User> selectByMoreLike(String uname, Integer cid) {
        return user1Mapper.selectByMoreLike(uname, cid);
    }

    public Page<User> selectByMoreLikePage(Integer pageNum, Integer pageSize, String uname, Integer cid) {
        Integer skipNum = (pageNum - 1) * pageSize;
        Page<User> page = new Page<>();
        List<User> userList = user1Mapper.selectByMoreLikePage(skipNum, pageSize, uname, cid);
        Integer total = user1Mapper.selectCountByPage(uname, cid);
        page.setTotal(total);
        page.setList(userList);
        return page;
    }

    public User login(User user) {
        User dbUser = user1Mapper.selectByUname(user.getUname());
        if (dbUser == null) {
            throw new ServiceException("用户名不存在");
        }
        if (!dbUser.getPsw().equals(user.getPsw())) {
            throw new ServiceException("用户名或密码错误");
        }
        //生成token（签名密钥必须与 JwtFilter 验签使用的库中密码一致）
        String token = TokenUtils.createToken(dbUser.getId().toString(), dbUser.getPsw());
        dbUser.setToken(token);
        return dbUser;
    }

    public User register(User user) {
        User dbUser = user1Mapper.selectByUname(user.getUname());
        if (dbUser != null) {
            throw new ServiceException("用户名已存在");
        }
        user1Mapper.insert(user);
        return user1Mapper.selectByUname(user.getUname());
    }

    public List<User> selectAllToExport() {
        return user1Mapper.selectAllToExport();
    }

    public List<User> selectByDeid(Integer deid) {
        if (deid == -1) {
            return user1Mapper.selectByDeidIsNull();
        } else {
            return user1Mapper.selectByDeid(deid);
        }
    }

    ;

    public List<User> selectByGid(Integer gid) {
        if (gid == -1) {
            return user1Mapper.selectByGidIsNull();
        } else {
            return user1Mapper.selectByGid(gid);
        }
    }

    public List<User> selectByBranch(Integer bid) {
        return user1Mapper.selectByBranch(bid);
    }

    public  List<User> selectAuditor() {return user1Mapper.selectAuditor();}

    public void updateDeid(User user) {
        user1Mapper.updateDeid(user);
    }

    public void updateGids(List<Integer> ids, Integer gid) {
        for (Integer id : ids) {
            user1Mapper.updateGid(id, gid);
        }

    }

    public void deleteGid(Integer id) {
        user1Mapper.deleteGid(id);
    }

    public List<User> selectBranchByGid(Integer gid) {
        List<Map<String, Object>> resultMaps = user1Mapper.selectBranchByGid(gid);
        List<User> userList = new ArrayList<>();
        for (Map<String, Object> resultMap : resultMaps) {
            User user = new User();
            user.setId((Integer) resultMap.get("id"));
            user.setUsername((String) resultMap.get("username"));
            userList.add(user);
        }
        return userList;
    }

    public void updateAvatar(User user) {
        user1Mapper.updateAvatar(user);
    }

    public void updateUsername(User user) {
        user1Mapper.updateUsername(user);
    }

    public void updateAccount(User user) {
        user1Mapper.updateAccount(user);
    }


}
