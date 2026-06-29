package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.PaperMCQuestionMapper;
import com.example.dangjian_spring.entity.PaperMultipleChoiceQuestion;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class PaperMCQuestionService extends ServiceImpl<PaperMCQuestionMapper, PaperMultipleChoiceQuestion> {
    @Resource
    private PaperMCQuestionMapper paperMCQuestionMapper;

    public List<PaperMultipleChoiceQuestion> allMCQuestionsInPaper(int id)
    {
        return paperMCQuestionMapper.allMCQuestionsInPaper(id);
    }

    public int addMCQuestionInPaper(PaperMultipleChoiceQuestion paperMultipleChoiceQuestion)
    {
        int n = paperMCQuestionMapper.countMcInPaper(
                paperMultipleChoiceQuestion.getTest_paper_id(),
                paperMultipleChoiceQuestion.getMultiple_choice_question_id());
        if (n > 0) {
            return 1;
        }
        return paperMCQuestionMapper.addMCQuestionInPaper(paperMultipleChoiceQuestion);
    }

    public int deleteMCQuestionInPaper(PaperMultipleChoiceQuestion paperTrueFalseQuestion)
    {
        return paperMCQuestionMapper.deleteMCQuestionInPaper(paperTrueFalseQuestion);
    }

    public int updateMCQuestionInPaper(PaperMultipleChoiceQuestion paperTrueFalseQuestion)
    {
        return paperMCQuestionMapper.updateMCQuestionInPaper(paperTrueFalseQuestion);
    }


}
