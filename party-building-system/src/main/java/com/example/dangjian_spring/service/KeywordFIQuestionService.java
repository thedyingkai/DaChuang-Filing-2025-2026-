package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.KeywordFIQuestionMapper;
import com.example.dangjian_spring.entity.KeywordFillInTheBlankQuestion;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class KeywordFIQuestionService extends ServiceImpl<KeywordFIQuestionMapper, KeywordFillInTheBlankQuestion> {
    @Resource
    private KeywordFIQuestionMapper keywordFIQuestionMapper;

    public List<KeywordFillInTheBlankQuestion> selectKeywordFIQuestionByKeywordId(int id)
    {
        return keywordFIQuestionMapper.selectKeywordFIQuestionByKeywordId(id);
    }

    public List<KeywordFillInTheBlankQuestion> selectKeywordFIQuestionByQuestionId(int id)
    {
        return keywordFIQuestionMapper.selectKeywordFIQuestionByFIQuestionId(id);
    }

    public int addKeywordFIQuestion(KeywordFillInTheBlankQuestion keywordTrueFalseQuestion)
    {
        return keywordFIQuestionMapper.addKeywordInFIQuestion(keywordTrueFalseQuestion);
    }

    public int deleteKeywordFIQuestion(KeywordFillInTheBlankQuestion keywordTrueFalseQuestion)
    {
        return keywordFIQuestionMapper.deleteKeywordFIQuestion(keywordTrueFalseQuestion);
    }
}
