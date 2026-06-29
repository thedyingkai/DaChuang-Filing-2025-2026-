package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.AuditMapper;
import com.example.dangjian_spring.dao.mapper.DraftMapper;
import com.example.dangjian_spring.dao.mapper.SubmitMapper;
import com.example.dangjian_spring.exception.ServiceException;
import com.example.dangjian_spring.entity.Audit;
import com.example.dangjian_spring.entity.Submit;
import com.example.dangjian_spring.entity.Process;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;


@Service
@Transactional
public class AuditService extends ServiceImpl<AuditMapper, Audit> {

    @Resource
    private AuditMapper auditMapper;

    @Resource
    private ArticleService articleService;

    @Resource
    private SubmitMapper submitMapper;

    @Resource
    private DraftMapper draftMapper;

    public void addAuditList(List<Process> processList, Submit submit) {
        if (processList == null || processList.isEmpty()) {
            return;
        }
        for (Process p : processList) {
            if (p == null || p.getUid() == null) {
                throw new ServiceException("400", "审核流程中存在未配置审核员的节点，无法生成待审任务。请在「审核流程」中为该栏目流程指定审核人。");
            }
        }
        int next = 0;
        if (processList.size() == 1) {
            Audit audit = new Audit();
            audit.setSrid(submit.getId());
            audit.setUid(processList.get(0).getUid());
            audit.setStatus(1);
            audit.setTime(submit.getTime());
            auditMapper.add(audit);
        } else {
            for (int i = processList.size() - 1; i >= 0; i--) {
                Audit audit = new Audit();
                audit.setSrid(submit.getId());
                audit.setUid(processList.get(i).getUid());
                if (i == processList.size() - 1) {
                    audit.setStatus(0);
                } else if (i == 0) {
                    audit.setStatus(1);
                    audit.setTime(submit.getTime());
                    audit.setNext(next);
                } else {
                    audit.setStatus(0);
                    audit.setNext(next);
                }
                auditMapper.add(audit);
                next = audit.getId();
            }
        }
    }

    public void update(Audit audit) {
        auditMapper.updateById(audit);
        Submit submit = new Submit();
        submit.setId(audit.getSrid());
        submit.setStatus(audit.getStatus());
        if (audit.getStatus() == 2) {
            // next 为链上下一待审 audit 的主键；库中可能为 0，须与 null 同样视为「无下一级」
            if (audit.getNext() != null && audit.getNext() > 0) {
                auditMapper.updateNextStatus(audit.getNext());
                Submit current = submitMapper.selectById(audit.getSrid());
                if (current != null && current.getDid() != null) {
                    draftMapper.unlockDraft(current.getDid());
                }
            } else {
                Submit current = submitMapper.selectById(audit.getSrid());
                if (current == null || current.getDid() == null) {
                    throw new IllegalStateException("submit not found for audit: " + audit.getSrid());
                }
                articleService.publishFromSubmit(audit.getSrid(), audit.getTime());
                draftMapper.updateStatusById(current.getDid(), 2);
                submitMapper.updateSubmitStatus(submit);
            }
        } else if (audit.getStatus() == 3) {
            Submit current = submitMapper.selectById(audit.getSrid());
            if (current != null && current.getDid() != null) {
                draftMapper.updateStatusById(current.getDid(), 0);
            }
            submitMapper.updateSubmitStatus(submit);
        }
    }

    public int getCidByCname(String cname) {
        return auditMapper.getCidByCname(cname);
    }

    public void insertAudit(Audit audit) {
        auditMapper.insert(audit);
    }

    public void updateAudit(Audit audit) {
        auditMapper.updateAudit(audit);
    }

    public void deleteAudit(Integer cid) {
        auditMapper.deleteAudit(cid);
    }

    public void batchDeleteAudit(List<Integer> cids) {
        for (Integer cid : cids) {
            auditMapper.deleteAudit(cid);
        }
    }

    public List<Audit> selectAll() {
        return auditMapper.selectAll();
    }

    public Audit selectByCid(Integer cid) {
        return auditMapper.selectByCid(cid);
    }

    public Audit selectByCname(String cname) {
        return auditMapper.selectByCname(cname);
    }

    public List<Audit> selectByUid(Integer uid) {
        return auditMapper.selectByUid(uid);
    }

    public List<Audit> selectAuditedByUid(Integer uid) {
        return auditMapper.selectAuditedByUid(uid);
    }

    public List<Audit> selectBySubmitId(Integer id) {return auditMapper.selectBySubmitId(id);
    }
}