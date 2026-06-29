package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.common.Page;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.dao.mapper.KeywordMapper;
import com.example.dangjian_spring.entity.Keyword;
import com.example.dangjian_spring.exception.ServiceException;
import com.example.dangjian_spring.utils.TokenUtils;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class KeywordService extends ServiceImpl<KeywordMapper, Keyword>{
    @Resource
    private KeywordMapper keywordMapper;

    public List<Keyword> allKeywords()
    {
        return keywordMapper.allKeywords();
    }

    public List<Keyword> selectKeywordsInTFQuestion(int id)
    {
        return keywordMapper.selectKeywordsInTFQuestion(id);
    }

    public List<Keyword> selectKeywordInMCQuestion(int id)
    {
        return keywordMapper.selectKeywordsInMCQuestion(id);
    }

    public List<Keyword> selectKeywordInFIQuestion(int id)
    {
        return keywordMapper.selectKeywordsInFIQuestion(id);
    }


    public List<Keyword> searchKeyword(String keyword)
    {
        keyword = "%" + keyword + "%";
        return keywordMapper.searchKeywordByDes(keyword);
    }

    public List<Keyword> selectKeywordsSameColumn(int id)
    {
        return keywordMapper.selectKeywordsSameColumn(id);
    }

    public int addKeyword(Keyword k)
    {
        return keywordMapper.addKeyword(k);
    }

    public int updateKeyword(Keyword k)
    {
        return keywordMapper.updateKeyword(k);
    }

    public int deleteKeyword(int id)
    {
        return keywordMapper.deleteKeyword(id);
    }

    public Keyword selectKeywordById(int id)
    {
        return keywordMapper.selectKeywordById(id);
    }

    public Keyword selectKeywordByDes(String description)
    {
        return keywordMapper.selectKeywordByDes(description);
    }

}
