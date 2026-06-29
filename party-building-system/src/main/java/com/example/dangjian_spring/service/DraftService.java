package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.Controller.SubmitController;
import com.example.dangjian_spring.common.Page;
import com.example.dangjian_spring.dao.mapper.DraftMapper;
import com.example.dangjian_spring.entity.Draft;
import com.example.dangjian_spring.entity.Process;
import com.example.dangjian_spring.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;


@Service
@Transactional
public class DraftService extends ServiceImpl<DraftMapper, Draft> {

    @Resource
    DraftMapper draftMapper;

    @Resource
    SubmitController submitController;

    @Resource
    private ProcessService processService;

    public void insertDraft(Draft draft) {
        this.draftMapper.insert(draft);
    }

    public void updateDraft(Draft draft) {
        this.draftMapper.updateDraft(draft);
    }

    public void deleteDraft(Integer id) {
        this.draftMapper.deleteDraft(id);
    }

    public void batchDeleteDraft(List<Integer> ids) {
        for (Integer id : ids) {
            draftMapper.deleteDraft(id);
        }
    }

    public List<Draft> selectAll() {
        return draftMapper.selectAll();
    }

    public Draft selectByDid(Integer id) {
        return draftMapper.selectById(id);
    }

    public Draft selectByTitle(String title) {
        return draftMapper.selectByTitle(title);
    }

    public Draft selectByMore(String title, Integer cid) {
        return draftMapper.selectByMore(title, cid);
    }

    public List<Draft> selectByMoreLike(String title, Integer cid) {
        return draftMapper.selectByMoreLike(title, cid);
    }

    public Page<Draft> selectByMoreLikePage(Integer pageNum, Integer pageSize, String title, Integer cid) {
        Integer skipNum = (pageNum - 1) * pageSize;
        Page<Draft> page = new Page<>();
        List<Draft> draftList = draftMapper.selectByMoreLikePage(skipNum, pageSize, title, cid);
        Integer total = draftMapper.selectCountByPage(title, cid);
        page.setTotal(total);
        page.setList(draftList);
        return page;
    }

    public List<Draft> selectByUid(Integer uid) {
        return draftMapper.selectByUid(uid);
    }

    public List<Draft> selectSubByUid(Integer uid) {
        return draftMapper.selectSubByUid(uid);
    }

    public void newAndSubmit(Draft draft) {
        if (draft.getCoid() == 0) {
            draft.setCoid(-1);
        }
        assertDraftHasAuditProcess(draft);
        this.draftMapper.insertAndSubmit(draft);
        submitController.add(draft);

    }

    public List<Draft> selectLocalByUid(Integer uid) {
        return draftMapper.selectLocalByUid(uid);
    }

    public void submit(Draft draft) {
        assertDraftHasAuditProcess(draft);
        this.draftMapper.submit(draft);
        submitController.add(draft);
    }

    private void assertDraftHasAuditProcess(Draft draft) {
        if (draft.getCoid() == null || draft.getCoid() <= 0) {
            throw new ServiceException("400", "请先选择有效栏目后再提交。");
        }
        List<Process> processList = processService.selectByCoid(draft.getCoid(), draft.getBid());
        if (processList == null || processList.isEmpty()) {
            throw new ServiceException("400", "该栏目未配置审核流程或未绑定审核人。请管理员在「审核流程」中为该栏目绑定流程。");
        }
    }

    public List<Draft> selectAllToAudit() {
        return draftMapper.selectAllToAudit();
    }

    public String selectSendTimeById(Integer did) {
        return draftMapper.selectSendTimeById(did);
    }


    public void updateStatusById(Integer did, Integer status) {
        this.draftMapper.updateStatusById(did, status);
    }

    public void lockDraft(Integer id) {
        this.draftMapper.lockDraft(id);
    }

    public void unlockDraft(Integer id) {
        this.draftMapper.unlockDraft(id);
    }

    public Integer checkStatus(Integer id) {
        return draftMapper.checkStatus(id);
    }

    public List<Draft> selectAuditedByUid(Integer uid) {
        return draftMapper.selectAuditedByUid(uid);
    }
}
