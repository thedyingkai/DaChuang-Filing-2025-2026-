package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.PaperFIQuestionMapper;
import com.example.dangjian_spring.entity.PaperFillInTheBlankQuestion;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class PaperFIQuestionService extends ServiceImpl<PaperFIQuestionMapper, PaperFillInTheBlankQuestion> {
    @Resource
    private PaperFIQuestionMapper paperFIQuestionMapper;

    public List<PaperFillInTheBlankQuestion> allFIQuestionsInPaper(int id)
    {
        return paperFIQuestionMapper.allFIQuestionsInPaper(id);
    }

    public int addFIQuestionInPaper(PaperFillInTheBlankQuestion paperFillInTheBlankQuestion)
    {
        int n = paperFIQuestionMapper.countFiInPaper(
                paperFillInTheBlankQuestion.getTest_paper_id(),
                paperFillInTheBlankQuestion.getFill_in_the_blank_question_id());
        if (n > 0) {
            return 1;
        }
        return paperFIQuestionMapper.addFIQuestionInPaper(paperFillInTheBlankQuestion);
    }

    public int deleteFIQuestionInPaper(PaperFillInTheBlankQuestion paperFillInTheBlankQuestion)
    {
        return paperFIQuestionMapper.deleteFIQuestionInPaper(paperFillInTheBlankQuestion);
    }

    public int updateFIQuestionInPaper(PaperFillInTheBlankQuestion paperFillInTheBlankQuestion)
    {
        return paperFIQuestionMapper.updateFIQuestionInPaper(paperFillInTheBlankQuestion);
    }


}
