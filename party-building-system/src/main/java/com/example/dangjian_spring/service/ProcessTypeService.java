package com.example.dangjian_spring.service;
import com.example.dangjian_spring.dao.mapper.ProcessAuditorMapper;
import com.example.dangjian_spring.dao.mapper.ProcessMapper;
import com.example.dangjian_spring.dao.mapper.Processtype_to_columnMapper;
import com.example.dangjian_spring.entity.ProcessType;
import com.example.dangjian_spring.entity.Process;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.ProcessTypeMapper;
import com.example.dangjian_spring.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import jakarta.annotation.Resource;

@Service
public class ProcessTypeService extends ServiceImpl<ProcessTypeMapper, ProcessType> {
    @Resource
    private ProcessTypeMapper processTypeMapper;

    @Resource
    private ProcessMapper processMapper;
    @Resource
    private ProcessAuditorMapper processAuditorMapper;
    @Resource
    private Processtype_to_columnMapper processtype_to_columnMapper;
    @Autowired
    private ProcessService processService;


    public List<ProcessType> selectall(){
        return processTypeMapper.selectall();
    }

    public List<ProcessType> selectByBid(Integer bid){
        return processTypeMapper.selectByBid(bid);
    }

    public void add(ProcessType processType) {
        int rows = processService.addnew(processType.getUid());
            processType.setPid(rows);
            processTypeMapper.insert(processType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        if (id == null) {
            throw new ServiceException("400", "无效的方案 id");
        }
        if (Integer.valueOf(1).equals(id)) {
            throw new ServiceException("400", "默认方案不可删除");
        }
        ProcessType processType = processTypeMapper.selectById(id);
        if (processType == null) {
            throw new ServiceException("404", "审核方案不存在或已删除");
        }
        // 先解除栏目绑定，否则外键会阻止删除 processtype 行
        processtype_to_columnMapper.deleteByptid(id);
        // 从链尾到链头删除流程节点（含审核员关联），再删方案头指针
        List<Process> chain = processService.selectByptid(id);
        // 先解除 processtype → process 外键，否则删到链头节点时会失败
        processTypeMapper.clearPidByPtid(id);
        if (chain != null && !chain.isEmpty()) {
            for (int i = chain.size() - 1; i >= 0; i--) {
                Integer nodeId = chain.get(i).getId();
                if (nodeId != null) {
                    processAuditorMapper.delete(nodeId);
                    processMapper.delete(nodeId);
                }
            }
        } else if (processType.getPid() != null) {
            processAuditorMapper.delete(processType.getPid());
            processMapper.delete(processType.getPid());
        }
        processTypeMapper.delete(id);
    }

    public void setType(Integer id){
        processTypeMapper.typesetzero();
        processTypeMapper.typesetone(id);
    }

    public void setCoid(ProcessType processType){

        //此代码保证一个审核方案只配置给一个栏目
        processtype_to_columnMapper.deleteByptid(processType.getId());
        //保证一个支部的某栏目只配置一个方案
        processtype_to_columnMapper.deleteBycoid(processType.getCoid(),processType.getBid());
        processtype_to_columnMapper.add(processType.getId(),processType.getCoid(),processType.getBid());

    }

}
