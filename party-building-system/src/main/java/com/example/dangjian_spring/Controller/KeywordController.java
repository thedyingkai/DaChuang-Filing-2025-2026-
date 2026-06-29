package com.example.dangjian_spring.Controller;

import cn.hutool.core.util.StrUtil;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Keyword;
import com.example.dangjian_spring.service.KeywordService;
import com.example.dangjian_spring.service.Column2Service;
import com.example.dangjian_spring.service.ColumnService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
public class KeywordController {
    @Resource
    KeywordService keywordService;
    @Resource
    Column2Service column2Service;
    @Resource
    ColumnService columnService;

    /** 关键词归属栏目：兼容 column2 表与资讯栏目 column_view */
    private boolean columnIdValid(Integer columnId) {
        if (columnId == null) {
            return false;
        }
        if (column2Service.selectColumnById(columnId) != null) {
            return true;
        }
        return columnService.selectByCid(columnId) != null;
    }

    @GetMapping("/keywords")
    public Result allKeywords()
    {
        return Result.success(keywordService.allKeywords());
    }

    @GetMapping("/keywords/in_tf/{id}")
    public Result selectKeywordsInTFQuestion(@PathVariable int id)
    {
        return Result.success(keywordService.selectKeywordsInTFQuestion(id));
    }

    @GetMapping("/keywords/in_mc/{id}")
    public Result selectKeywordsInMCQuestion(@PathVariable int id)
    {
        return Result.success(keywordService.selectKeywordInMCQuestion(id));
    }

    @GetMapping("/keywords/in_fi/{id}")
    public Result selectKeywordsInFIQuestion(@PathVariable int id)
    {
        return Result.success(keywordService.selectKeywordInFIQuestion(id));
    }

    @GetMapping("/keywords/delete/{id}")
    public Result deleteKeyword(@PathVariable int id)
    {
        if(keywordService.selectKeywordById(id) != null)
        {
            if(keywordService.deleteKeyword(id) != 0)
            {
                return Result.success("删除成功");
            }
            else
            {
                return Result.error("删除失败");
            }
        }
        else
        {
            return Result.error("关键词" + id + "不存在");
        }
    }

    @GetMapping("/keywords/search/{keyword}")
    public Result searchKeywords(@PathVariable String keyword)
    {
        List<Keyword> keywords = keywordService.searchKeyword(keyword);
        return Result.success(keywords);
    }

    @GetMapping("/keywords/in_column/{id}")
    public Result selectKeywordsSameColumn(@PathVariable int id)
    {
        if (!columnIdValid(id)) {
            return Result.error("栏目" + id + "不存在");
        }
        return Result.success(keywordService.selectKeywordsSameColumn(id));
    }

    @PostMapping("/keywords/add")
    public Result addKeyword(@RequestBody Keyword keyword)
    {
        if (keyword == null || StrUtil.isBlank(keyword.getKeyword_description())) {
            return Result.error("请填写关键词描述");
        }
        String des = keyword.getKeyword_description().trim();
        keyword.setKeyword_description(des);
        if (keywordService.selectKeywordByDes(des) != null) {
            return Result.error("关键词「" + des + "」已存在");
        }
        if (!column2Service.ensureColumn2ExistsForKeyword(keyword.getColumn_id())) {
            return Result.error("请选择有效栏目");
        }
        if (keywordService.addKeyword(keyword) != 0) {
            return Result.success(keyword);
        }
        return Result.error("添加失败");
    }

    @PostMapping("/keywords/update")
    public Result updateKeyword(@RequestBody Keyword keyword)
    {
        if(keywordService.selectKeywordById(keyword.getKeyword_id()) == null)
        {
            return Result.error("关键词" + keyword.getKeyword_id() + "不存在");
        }
        else
        {
            if (!column2Service.ensureColumn2ExistsForKeyword(keyword.getColumn_id())) {
                return Result.error("栏目" + keyword.getColumn_id() + "不存在");
            }
            if (keywordService.updateKeyword(keyword) != 0) {
                return Result.success(keywordService.allKeywords());
            }
            return Result.error("删除失败");
        }
    }




































}
