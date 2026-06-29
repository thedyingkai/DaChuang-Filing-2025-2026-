package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.Branch_managerMapper;
import com.example.dangjian_spring.entity.Branch_manager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;

@Service
@Transactional
public class Branch_managerService extends ServiceImpl<Branch_managerMapper, Branch_manager> {
    @Resource
    private Branch_managerMapper branch_managerMapper;

    public List<Branch_manager> selectByBid(Integer bid) {
        return branch_managerMapper.selectByBid(bid);
    }

    public void add(Branch_manager branch_manager) {
        branch_managerMapper.add(branch_manager);
    }

    public void rename(Branch_manager branch_manager) {
        branch_managerMapper.rename(branch_manager);
    }

    public void delete(Integer id) {
        branch_managerMapper.delete(id);
    }

    public void changeManager(int id, int uid) {
        branch_managerMapper.changeManager(id, uid);
    }

    public void batchAdd(List<Branch_manager> branchManagers) {
        for (Branch_manager branch_manager : branchManagers) {
            branch_managerMapper.add(branch_manager);
        }
    }
}
