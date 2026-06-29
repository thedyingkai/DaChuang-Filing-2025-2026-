package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.MCQuestionMapper;
import com.example.dangjian_spring.dao.mapper.PaperMCQuestionMapper;
import com.example.dangjian_spring.entity.MultipleChoiceQuestion;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class MCQuestionService extends ServiceImpl<MCQuestionMapper, MultipleChoiceQuestion>{
    @Resource
    private MCQuestionMapper mcQuestionMapper;
    @Resource
    private PaperMCQuestionMapper paperMCQuestionMapper;

    public List<MultipleChoiceQuestion> listAllQuestions()
    {
        return mcQuestionMapper.selectAllMCQuestions();
    }

    public List<MultipleChoiceQuestion> listAllQuestionsInPaper(int id)
    {
        return mcQuestionMapper.selectAllMCQuestionsInPaper(id);
    }

    public List<MultipleChoiceQuestion> searchQuestionsByDescription(String keyword)
    {
        keyword = "%" + keyword + "%";
        return mcQuestionMapper.searchMCQuestionsDescription(keyword);
    }

    public List<MultipleChoiceQuestion> searchQuestionByAnalysis(String keyword)
    {
        keyword = "%" + keyword + "%";
        return mcQuestionMapper.searchMCQuestionsAnalysis(keyword);
    }

    public List<MultipleChoiceQuestion> searchQuestionByKeyword(String keyword)
    {
        keyword = "%" + keyword + "%";
        return mcQuestionMapper.searchMCQuestionsKeyword(keyword);
    }

    public MultipleChoiceQuestion selectQuestionById(int id)
    {
        return mcQuestionMapper.selectMCQuestionById(id);
    }

    public MultipleChoiceQuestion selectQuestionByDes(String description)
    {
        return mcQuestionMapper.selectMCQuestionByDes(description);
    }

    public int addQuestion(MultipleChoiceQuestion question)
    {
        return mcQuestionMapper.addMCQuestion(question);
    }

    public int updateQuestion(MultipleChoiceQuestion question)
    {
        return mcQuestionMapper.updateMCQuestion(question);
    }

    public int deleteQuestion(int id)
    {
        paperMCQuestionMapper.deletePaperLinksByMcQuestionId(id);
        return mcQuestionMapper.deleteMCQuestionById(id);
    }

}
