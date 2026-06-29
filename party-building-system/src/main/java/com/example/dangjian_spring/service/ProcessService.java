package com.example.dangjian_spring.service;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.ProcessAuditorMapper;
import com.example.dangjian_spring.dao.mapper.ProcessMapper;

import com.example.dangjian_spring.dao.mapper.ProcessTypeMapper;
import com.example.dangjian_spring.entity.Process;
import com.example.dangjian_spring.entity.ProcessType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProcessService extends ServiceImpl<ProcessMapper, Process> {
    @Resource
    private ProcessMapper processMapper;

    @Resource
    private ProcessTypeMapper processTypeMapper;

    @Resource
    private ProcessAuditorMapper processAuditorMapper;

    public int addnew(Integer uid){
        Process holder = new Process();
        processMapper.add(holder);
        Integer pid = holder.getId();
        processAuditorMapper.add(pid, uid);
        return pid;
    }


    public void add(Process process) {
        Process holder = new Process();
        processMapper.add(holder);
        Integer rows = holder.getId();
        processAuditorMapper.add(rows, process.getUid());
        Process beforeprocess = new Process();
        beforeprocess.setNext(rows);
        beforeprocess.setId(process.getBefore());
        processMapper.removeLast(beforeprocess);
    }

    public void delete(Integer id) {
        Process process=processMapper.selectById(id);
        Process beforeprocess=processMapper.selectByNext(process.getId());
        if (beforeprocess!=null) {
            processMapper.setLast(beforeprocess.getId());
        }
        Integer next = process.getNext();
        if (next != null) {
            processTypeMapper.rewirePidHead(id, next);
        } else {
            processTypeMapper.clearPidByProcessHead(id);
        }
        processAuditorMapper.delete(id);
        processMapper.delete(id);


    }

    public void updateAuditor(Process process) {
        processAuditorMapper.delete(process.getId());
        processAuditorMapper.add(process.getId(),process.getUid());
    }

    public List<Process> selectAll(){
        return processMapper.selectAll();
    }

    public List<Process> selectByptid(Integer id){
        ProcessType processType=processTypeMapper.selectById(id);
        List<Process> processList=new ArrayList<>();
        if(processType!=null){
            buildlist(processType.getPid(),processList);
        }
        return processList;
    }

    public List<Process> selectBytypeone(){
        ProcessType processType=processTypeMapper.selectByType();
        List<Process> processList=new ArrayList<>();
        if(processType!=null){
            buildlist(processType.getPid(),processList);
        }
        return processList;
    };

    public List<Process> selectByCoid(Integer coid, Integer bid) {
        ProcessType processType = null;
        // 前端可能传占位 -1；无效组织 id 不应参与「按栏目+支部」精确匹配
        if (bid != null && bid > 0) {
            processType = processTypeMapper.selectByCoid(coid, bid);
        }
        if (processType == null) {
            processType = processTypeMapper.selectFirstByCoid(coid);
        }
        List<Process> processList = new ArrayList<>();
        if (processType != null && processType.getPid() != null) {
            buildlist(processType.getPid(), processList);
        }
        return processList;
    }


    public void buildlist(Integer id,List<Process> processList){
    Process process=processMapper.selectById(id);
    processList.add(process);
    if (process.getLast()!=1 && process.getNext()!=null) {
        buildlist(process.getNext(),processList);
    }

    }


}
