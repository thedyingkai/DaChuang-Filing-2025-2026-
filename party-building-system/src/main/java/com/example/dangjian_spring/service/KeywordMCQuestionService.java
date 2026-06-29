package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.KeywordMCQuestionMapper;
import com.example.dangjian_spring.entity.KeywordMultipleChoiceQuestion;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class KeywordMCQuestionService extends ServiceImpl<KeywordMCQuestionMapper, KeywordMultipleChoiceQuestion> {
    @Resource
    private KeywordMCQuestionMapper keywordMCQuestionMapper;

    public List<KeywordMultipleChoiceQuestion> selectKeywordMCQuestionByKeywordId(int id)
    {
        return keywordMCQuestionMapper.selectKeywordMCQuestionByKeywordId(id);
    }

    public List<KeywordMultipleChoiceQuestion> selectKeywordMCQuestionByQuestionId(int id)
    {
        return keywordMCQuestionMapper.selectKeywordMCQuestionByTFQuestionId(id);
    }

    public int addKeywordMCQuestion(KeywordMultipleChoiceQuestion keywordTrueFalseQuestion)
    {
        return keywordMCQuestionMapper.addKeywordInMCQuestion(keywordTrueFalseQuestion);
    }

    public int deleteKeywordMCQuestion(KeywordMultipleChoiceQuestion keywordTrueFalseQuestion)
    {
        return keywordMCQuestionMapper.deleteKeywordMCQuestion(keywordTrueFalseQuestion);
    }

    public int deleteKeywordByMCQuestionId(int id)
    {
        return keywordMCQuestionMapper.deleteKeywordByMCQuestionId(id);
    }

}
