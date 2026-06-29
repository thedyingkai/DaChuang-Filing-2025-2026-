package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.AuditMapper;
import com.example.dangjian_spring.dao.mapper.SubmitMapper;
import com.example.dangjian_spring.entity.Submit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

@Service
@Transactional
public class SubmitService extends ServiceImpl<SubmitMapper, Submit> {
    @Resource
    private SubmitMapper submitMapper;

    @Resource
    private AuditMapper auditMapper;

    public int add(Submit submit) {
        submitMapper.addSubmit(submit);
        return submit.getId();
    }

    public int update(Submit submit){
        return submitMapper.updateContent(submit);
    }



}
