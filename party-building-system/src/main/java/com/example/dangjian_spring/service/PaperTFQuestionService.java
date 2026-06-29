package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.PaperTFQuestionMapper;
import com.example.dangjian_spring.entity.PaperTrueFalseQuestion;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class PaperTFQuestionService extends ServiceImpl<PaperTFQuestionMapper, PaperTrueFalseQuestion> {
    @Resource
    private PaperTFQuestionMapper paperTFQuestionMapper;

    public List<PaperTrueFalseQuestion> allTFQuestionsInPaper(int id)
    {
        return paperTFQuestionMapper.allTFQuestionsInPaper(id);
    }

    public int addTFQuestionInPaper(PaperTrueFalseQuestion paperTrueFalseQuestion)
    {
        int n = paperTFQuestionMapper.countTfInPaper(
                paperTrueFalseQuestion.getTest_paper_id(),
                paperTrueFalseQuestion.getTrue_false_question_id());
        if (n > 0) {
            return 1;
        }
        return paperTFQuestionMapper.addTFQuestionInPaper(paperTrueFalseQuestion);
    }

    public int deleteTFQuestionInPaper(PaperTrueFalseQuestion paperTrueFalseQuestion)
    {
        return paperTFQuestionMapper.deleteTFQuestionInPaper(paperTrueFalseQuestion);
    }

    public int updateTFQuestionInPaper(PaperTrueFalseQuestion paperTrueFalseQuestion)
    {
        return paperTFQuestionMapper.updateTFQuestionInPaper(paperTrueFalseQuestion);
    }


}
