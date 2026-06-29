package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.date.DateUtil;
import com.example.dangjian_spring.dao.mapper.PaperMapper;
import com.example.dangjian_spring.entity.TestPaper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service

public class PaperService extends ServiceImpl<PaperMapper, TestPaper> {
    /** 与前端「在线自测」提交参与记录时使用的占位试卷标题（勿改，否则无法关联历史记录） */
    public static final String SELF_EXAM_PAPER_DESCRIPTION = "【系统】在线自测（勿删）";

    @Resource
    private PaperMapper paperMapper;

    public List<TestPaper> allPapers()
    {
        return paperMapper.allPapers();
    }

    public List<TestPaper> searchPapersByDes(String keyword)
    {
        keyword = "%" + keyword + "%";
        return paperMapper.searchPaperByDes(keyword);
    }

    public List<TestPaper> searchPaperByMethod(String keyword)
    {
        keyword = "%" + keyword + "%";
        return paperMapper.searchPaperByMethod(keyword);
    }

    public TestPaper selectPaperById(int id)
    {
        return paperMapper.selectPaperById(id);
    }

    public TestPaper selectPaperByDes(String description)
    {
        return paperMapper.selectPaperByDes(description);
    }

    public int addPaper(TestPaper paper)
    {
        return paperMapper.addPaper(paper);
    }

    public int deletePaper(int id)
    {
        return paperMapper.deletePaper(id);
    }

    public int updatePaper(TestPaper paper)
    {
        return paperMapper.updatePaper(paper);
    }

    /**
     * 获取或创建「在线自测」占位试卷，用于 participation_o 外键与按用户汇总自测记录。
     */
    public TestPaper getOrCreateSelfExamPaper() {
        TestPaper existing = paperMapper.selectPaperByDes(SELF_EXAM_PAPER_DESCRIPTION);
        if (existing != null) {
            return existing;
        }
        TestPaper p = new TestPaper();
        p.setPaper_description(SELF_EXAM_PAPER_DESCRIPTION);
        p.setGrouping_method("self_exam");
        p.setPoints_reward(0);
        p.setCreate_date(DateUtil.now());
        p.setDeadline("2099-12-31 23:59:59");
        if (paperMapper.addPaper(p) == 0) {
            return paperMapper.selectPaperByDes(SELF_EXAM_PAPER_DESCRIPTION);
        }
        if (p.getTest_paper_id() == null) {
            return paperMapper.selectPaperByDes(SELF_EXAM_PAPER_DESCRIPTION);
        }
        return p;
    }

}
