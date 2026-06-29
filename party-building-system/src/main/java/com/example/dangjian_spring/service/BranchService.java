package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.BranchMapper;
import com.example.dangjian_spring.entity.Branch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;

@Service
@Transactional
public class BranchService extends ServiceImpl<BranchMapper, Branch> {
    @Resource
    private BranchMapper branchMapper;

    public List<Branch> selectAll() {
        return branchMapper.selectAll();
    }

    public Branch selectByBid(Integer bid) {return branchMapper.selectByBid(bid);}

    public void add(Branch branch) {
        branchMapper.add(branch);
    }

    public void delete(Integer id) {
        branchMapper.delete(id);
    }

    public void update(Branch branch) {
        branchMapper.update(branch);
    }

}
