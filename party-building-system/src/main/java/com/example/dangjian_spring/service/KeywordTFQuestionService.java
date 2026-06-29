package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.KeywordTFQuestionMapper;
import com.example.dangjian_spring.entity.KeywordTrueFalseQuestion;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class KeywordTFQuestionService extends ServiceImpl<KeywordTFQuestionMapper, KeywordTrueFalseQuestion> {
    @Resource
    private KeywordTFQuestionMapper keywordTFQuestionMapper;

    public List<KeywordTrueFalseQuestion> selectKeywordTFQuestionByKeywordId(int id)
    {
        return keywordTFQuestionMapper.selectKeywordTFQuestionByKeywordId(id);
    }

    public List<KeywordTrueFalseQuestion> selectKeywordTFQuestionByQuestionId(int id)
    {
        return keywordTFQuestionMapper.selectKeywordTFQuestionByTFQuestionId(id);
    }

    public int addKeywordTFQuestion(KeywordTrueFalseQuestion keywordTrueFalseQuestion)
    {
        return keywordTFQuestionMapper.addKeywordInTFQuestion(keywordTrueFalseQuestion);
    }

    public int deleteKeywordTFQuestion(KeywordTrueFalseQuestion keywordTrueFalseQuestion)
    {
        return keywordTFQuestionMapper.deleteKeywordTFQuestion(keywordTrueFalseQuestion);
    }

    public int deleteKeywordTFQuestionById(int id)
    {
        return keywordTFQuestionMapper.deleteKeywordByTFQuestionId(id);
    }
}
