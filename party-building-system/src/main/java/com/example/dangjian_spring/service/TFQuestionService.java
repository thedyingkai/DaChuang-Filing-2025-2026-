package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.PaperTFQuestionMapper;
import com.example.dangjian_spring.dao.mapper.TFQuestionMapper;
import com.example.dangjian_spring.entity.TrueFalseQuestion;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class TFQuestionService extends ServiceImpl<TFQuestionMapper, TrueFalseQuestion>{
    @Resource
    private TFQuestionMapper tfQuestionMapper;
    @Resource
    private PaperTFQuestionMapper paperTFQuestionMapper;

    public List<TrueFalseQuestion> listAllQuestions()
    {
        return tfQuestionMapper.selectAllTFQuestions();
    }

    public List<TrueFalseQuestion> listAllQuestionsInPaper(int id)
    {
        return tfQuestionMapper.selectAllTFQuestionsInPaper(id);
    }

    public List<TrueFalseQuestion> searchQuestionsByDescription(String keyword)
    {
        keyword = "%" + keyword + "%";
        return tfQuestionMapper.searchTFQuestionsDescription(keyword);
    }

    public List<TrueFalseQuestion> searchQuestionByAnalysis(String keyword)
    {
        keyword = "%" + keyword + "%";
        return tfQuestionMapper.searchTFQuestionsAnalysis(keyword);
    }

    public List<TrueFalseQuestion> searchQuestionByKeyword(String keyword)
    {
        keyword = "%" + keyword + "%";
        return tfQuestionMapper.searchTFQuestionsKeyword(keyword);
    }

    public TrueFalseQuestion selectQuestionById(int id)
    {
        return tfQuestionMapper.selectTFQuestionById(id);
    }

    public TrueFalseQuestion selectQuestionByDes(String description)
    {
        return tfQuestionMapper.selectTFQuestionByDes(description);
    }

    public int addQuestion(TrueFalseQuestion question)
    {
        return tfQuestionMapper.addTFQuestion(question);
    }

    public int updateQuestion(TrueFalseQuestion question)
    {
        return tfQuestionMapper.updateTFQuestion(question);
    }

    public int deleteQuestion(int id)
    {
        paperTFQuestionMapper.deletePaperLinksByTfQuestionId(id);
        return tfQuestionMapper.deleteTFQuestionById(id);
    }

}
