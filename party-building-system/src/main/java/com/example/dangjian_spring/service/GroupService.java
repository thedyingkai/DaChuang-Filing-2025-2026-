package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.GroupMapper;
import com.example.dangjian_spring.entity.Group;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;

@Service
@Transactional
public class GroupService extends ServiceImpl<GroupMapper, Group> {
    @Resource
    private GroupMapper groupMapper;

    public List<Group> selectByBid(Integer bid) {
        return groupMapper.selectByBid(bid);
    }

    public void add(Group group) {
        groupMapper.add(group);
    }

    public void update(Group group) {
        groupMapper.update(group);
    }

    public void setleader(Group group) {
        groupMapper.setleader(group);
    }

    public void delete(Integer gid) {
        groupMapper.delete(gid);
    }

}
