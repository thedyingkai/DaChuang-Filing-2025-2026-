package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.FIQuestionMapper;
import com.example.dangjian_spring.dao.mapper.PaperFIQuestionMapper;
import com.example.dangjian_spring.entity.FillInTheBlankQuestion;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class FIQuestionService extends ServiceImpl<FIQuestionMapper, FillInTheBlankQuestion>{
    @Resource
    private FIQuestionMapper fiQuestionMapper;
    @Resource
    private PaperFIQuestionMapper paperFIQuestionMapper;

    public List<FillInTheBlankQuestion> listAllQuestions()
    {
        return fiQuestionMapper.selectAllFIQuestions();
    }

    public List<FillInTheBlankQuestion> listAllQuestionsInPaper(int id)
    {
        return fiQuestionMapper.selectAllFIQuestionsInPaper(id);
    }

    public List<FillInTheBlankQuestion> searchQuestionsByDescription(String keyword)
    {
        keyword = "%" + keyword + "%";
        return fiQuestionMapper.searchFIQuestionsDescription(keyword);
    }

    public List<FillInTheBlankQuestion> searchQuestionByAnalysis(String keyword)
    {
        keyword = "%" + keyword + "%";
        return fiQuestionMapper.searchFIQuestionsAnalysis(keyword);
    }

    public List<FillInTheBlankQuestion> searchQuestionByKeyword(String keyword)
    {
        keyword = "%" + keyword + "%";
        return fiQuestionMapper.searchFIQuestionsKeyword(keyword);
    }

    public FillInTheBlankQuestion selectQuestionById(int id)
    {
        return fiQuestionMapper.selectFIQuestionById(id);
    }

    public FillInTheBlankQuestion selectQuestionByDes(String description)
    {
        return fiQuestionMapper.selectFIQuestionByDes(description);
    }

    public int addQuestion(FillInTheBlankQuestion question)
    {
        return fiQuestionMapper.addFIQuestion(question);
    }

    public int updateQuestion(FillInTheBlankQuestion question)
    {
        return fiQuestionMapper.updateFIQuestion(question);
    }

    public int deleteQuestion(int id)
    {
        paperFIQuestionMapper.deletePaperLinksByFiQuestionId(id);
        return fiQuestionMapper.deleteFIQuestionById(id);
    }

}
