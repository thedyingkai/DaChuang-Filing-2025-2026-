package com.example.dangjian_spring.Controller;

import cn.hutool.core.util.StrUtil;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.*;
import com.example.dangjian_spring.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
@Slf4j
public class QuestionController {
    @Resource
    TFQuestionService tfQuestionService;
    @Resource
    FIQuestionService fiQuestionService;
    @Resource
    MCQuestionService mcQuestionService;
    @Resource
    OptionService optionService;
    @Resource
    KeywordService keywordService;
    @Resource
    KeywordTFQuestionService keywordTFQuestionService;
    @Resource
    KeywordMCQuestionService keywordMCQuestionService;
    @Resource
    KeywordFIQuestionService keywordFIQuestionService;


    // 判断题相关
    @GetMapping("/questions/tf")
    public Result listTFQuestions()
    {
        return Result.success(tfQuestionService.listAllQuestions());
    }

    @GetMapping("/questions/tf/in_paper/{id}")
    public Result listTFQuestionsInPaper(@PathVariable int id)
    {
        return Result.success(tfQuestionService.listAllQuestionsInPaper(id));
    }


    @GetMapping("/questions/tf/search/des/{keyword}")
    public Result searchTFQuestionsDes(@PathVariable String keyword)
    {
        return Result.success(tfQuestionService.searchQuestionsByDescription(keyword));
    }

    @GetMapping("/questions/tf/search/anal/{keyword}")
    public Result searchTFQuestionsAnal(@PathVariable String keyword)
    {
        return Result.success(tfQuestionService.searchQuestionByAnalysis(keyword));
    }


    @GetMapping("/questions/tf/search/keyword/{keyword}")
    public Result searchTFQuestionsKeyword(@PathVariable String keyword)
    {
        return Result.success(tfQuestionService.searchQuestionByKeyword(keyword));
    }

    @GetMapping("/questions/tf/delete/{id}")
    public Result deleteTFQuestion(@PathVariable int id)
    {
        if(tfQuestionService.selectQuestionById(id) == null)
        {
            return Result.error("题目" + id + "不存在");
        }
        else
        {
            if(tfQuestionService.deleteQuestion(id) != 0 && keywordTFQuestionService.deleteKeywordTFQuestionById(id) != 0)
            {
                return Result.success("删除成功");

            }
            else
            {
                return Result.error("删除失败");
            }
        }
    }

    @PostMapping("/questions/tf/add/keyword")
    public Result addKeywordInTFQuestion(@RequestBody KeywordTrueFalseQuestion keywordTrueFalseQuestion)
    {
        log.debug("addKeywordInTFQuestion: {}", keywordTrueFalseQuestion);
        if(tfQuestionService.selectQuestionById(keywordTrueFalseQuestion.getTrue_false_question_id()) == null)
        {
            return Result.error("题目" + keywordTrueFalseQuestion.getTrue_false_question_id() + "不存在");
        }
        else
        {
            if(keywordService.selectKeywordById(keywordTrueFalseQuestion.getKeyword_id()) == null)
            {
                return Result.error("关键词" + keywordTrueFalseQuestion.getKeyword_id() + "不存在");
            }
            else
            {
                if(keywordTFQuestionService.addKeywordTFQuestion(keywordTrueFalseQuestion) != 0)
                {
                    return Result.success(keywordTFQuestionService.selectKeywordTFQuestionByQuestionId(keywordTrueFalseQuestion.getTrue_false_question_id()));
                }
                else
                {
                    return Result.error("添加失败");
                }
            }
        }
    }

    @PostMapping("/questions/tf/delete/keyword")
    public Result deleteKeywordInTFQuestion(@RequestBody KeywordTrueFalseQuestion keywordTrueFalseQuestion)
    {
        if(tfQuestionService.selectQuestionById(keywordTrueFalseQuestion.getTrue_false_question_id()) == null)
        {
            return Result.error("题目" + keywordTrueFalseQuestion.getTrue_false_question_id() + "不存在");
        }
        else
        {
            if(keywordService.selectKeywordById(keywordTrueFalseQuestion.getKeyword_id()) == null)
            {
                return Result.error("关键词" + keywordTrueFalseQuestion.getKeyword_id() + "不存在");
            }
            else
            {
                if(keywordTFQuestionService.deleteKeywordTFQuestion(keywordTrueFalseQuestion) != 0)
                {
                    return Result.success(keywordTFQuestionService.selectKeywordTFQuestionByQuestionId(keywordTrueFalseQuestion.getTrue_false_question_id()));
                }
                else
                {
                    return Result.error("删除失败");
                }
            }
        }
    }

    @PostMapping("/questions/tf/add")
    public Result addTFQuestion(@RequestBody TrueFalseQuestion question)
    {
        if (question == null || StrUtil.isBlank(question.getQuestion_description())) {
            return Result.error("题干不能为空");
        }
        if (StrUtil.isBlank(question.getCorrect_answer())) {
            return Result.error("请选择正确答案");
        }
        if (tfQuestionService.selectQuestionByDes(question.getQuestion_description().trim()) != null) {
            return Result.error("题目已存在：" + question.getQuestion_description());
        }
        question.setQuestion_description(question.getQuestion_description().trim());
        if (tfQuestionService.addQuestion(question) == 0) {
            return Result.error("添加失败");
        }
        if (question.getTrue_false_question_id() == null) {
            TrueFalseQuestion saved = tfQuestionService.selectQuestionByDes(question.getQuestion_description());
            return saved != null ? Result.success(saved) : Result.success(question);
        }
        return Result.success(question);
    }

    @PostMapping("/questions/tf/update")
    public Result updateTFQuestion(@RequestBody TrueFalseQuestion question)
    {
        if(tfQuestionService.updateQuestion(question) != 0)
        {
            return Result.success(tfQuestionService.listAllQuestions());
        }
        else
        {
            return Result.error("更新失败");
        }
    }

    // 选择题相关

    @GetMapping("/questions/mc")
    public Result listMCQuestions()
    {
        return Result.success(mcQuestionService.listAllQuestions());
    }

    @GetMapping("/questions/mc/in_paper/{id}")
    public Result selectAllMCQuestionsInpaper(@PathVariable int id)
    {
        return Result.success(mcQuestionService.listAllQuestionsInPaper(id));
    }

    @GetMapping("/questions/mc/search/des/{keyword}")
    public Result searchMCQuestionsDes(@PathVariable String keyword)
    {
        return Result.success(mcQuestionService.searchQuestionsByDescription(keyword));
    }

    @GetMapping("/questions/mc/search/anal/{keyword}")
    public Result searchMCQuestionsAnal(@PathVariable String keyword)
    {
        return Result.success(mcQuestionService.searchQuestionByAnalysis(keyword));
    }

    @GetMapping("/questions/mc/search/keyword/{keyword}")
    public Result searchMCQuestionsKeyword(@PathVariable String keyword)
    {
        return Result.success(mcQuestionService.searchQuestionByKeyword(keyword));
    }

    @GetMapping("/questions/mc/delete/{id}")
    public Result deleteMCQuestion(@PathVariable int id)
    {
        if(mcQuestionService.selectQuestionById(id) == null)
        {
            return Result.error("题目" + id + "不存在");
        }
        else
        {

            if(mcQuestionService.deleteQuestion(id) != 0 && keywordMCQuestionService.deleteKeywordByMCQuestionId(id) != 0)
            {
                return Result.success("删除成功");

            }
            else
            {
                return Result.error("删除失败");
            }
        }
    }


    @PostMapping("/questions/mc/add")
    public Result addMCQuestion(@RequestBody MultipleChoiceQuestion question)
    {
        if (question == null || StrUtil.isBlank(question.getQuestion_description())) {
            return Result.error("题干不能为空");
        }
        if (question.getCorrect_answer() == null) {
            return Result.error("请设置正确答案序号");
        }
        if (mcQuestionService.selectQuestionByDes(question.getQuestion_description().trim()) != null) {
            return Result.error("题目已存在：" + question.getQuestion_description());
        }
        question.setQuestion_description(question.getQuestion_description().trim());
        if (mcQuestionService.addQuestion(question) == 0) {
            return Result.error("添加失败");
        }
        if (question.getMultiple_choice_question_id() == null) {
            MultipleChoiceQuestion saved = mcQuestionService.selectQuestionByDes(question.getQuestion_description());
            return saved != null ? Result.success(saved) : Result.success(question);
        }
        return Result.success(question);
    }

    @PostMapping("/questions/mc/add/keyword")
    public Result addKeywordInMCQuestion(@RequestBody KeywordMultipleChoiceQuestion keywordMultipleChoiceQuestion)
    {
        if(mcQuestionService.selectQuestionById(keywordMultipleChoiceQuestion.getMultiple_choice_question_id()) == null)
        {
            return Result.error("题目" + keywordMultipleChoiceQuestion.getMultiple_choice_question_id() + "不存在");
        }
        else
        {
            if(keywordService.selectKeywordById(keywordMultipleChoiceQuestion.getKeyword_id()) == null)
            {
                return Result.error("关键词" + keywordMultipleChoiceQuestion.getKeyword_id() + "不存在");
            }
            else
            {
                if(keywordMCQuestionService.addKeywordMCQuestion(keywordMultipleChoiceQuestion) != 0)
                {
                    return Result.success(keywordMCQuestionService.selectKeywordMCQuestionByQuestionId(keywordMultipleChoiceQuestion.getMultiple_choice_question_id()));
                }
                else
                {
                    return Result.error("添加失败");
                }
            }
        }
    }

    @PostMapping("/questions/mc/delete/keyword")
    public Result deleteKeywordInMCQuestion(@RequestBody KeywordMultipleChoiceQuestion keywordMultipleChoiceQuestion)
    {
        if(mcQuestionService.selectQuestionById(keywordMultipleChoiceQuestion.getMultiple_choice_question_id()) == null)
        {
            return Result.error("题目" + keywordMultipleChoiceQuestion.getMultiple_choice_question_id() + "不存在");
        }
        else
        {
            if(keywordService.selectKeywordById(keywordMultipleChoiceQuestion.getKeyword_id()) == null)
            {
                return Result.error("关键词" + keywordMultipleChoiceQuestion.getKeyword_id() + "不存在");
            }
            else
            {
                if(keywordMCQuestionService.deleteKeywordMCQuestion(keywordMultipleChoiceQuestion) != 0)
                {
                    return Result.success(keywordMCQuestionService.selectKeywordMCQuestionByQuestionId(keywordMultipleChoiceQuestion.getMultiple_choice_question_id()));
                }
                else
                {
                    return Result.error("删除失败");
                }
            }
        }
    }

    @PostMapping("/questions/mc/update")
    public Result updateMCQuestion(@RequestBody MultipleChoiceQuestion question)
    {
        if(mcQuestionService.updateQuestion(question) != 0)
        {
            return Result.success("更新成功");
        }
        else
        {
            return Result.error("更新失败");
        }
    }

    // 选项相关

    @GetMapping("/options/in_question/{id}")
    public Result listOptions(@PathVariable int id)
    {
        if(mcQuestionService.selectQuestionById(id) != null)
        {
            return Result.success(optionService.allOptionInQuestion(id));
        }
        else
        {
            return Result.error("题目" + id + "不存在");
        }
    }

    @GetMapping("/options/delete/{id}")
    public Result deleteOption(@PathVariable int id)
    {
        if(optionService.selectOptionById(id) != null)
        {
            if(optionService.deleteOption(id) != 0)
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
            return Result.error("选项" + id + "不存在");
        }
    }

    @PostMapping("/options/update")
    public Result updateOption(@RequestBody MultipleChoiceOption option)
    {
        if(optionService.selectOptionById(option.getOption_id()) != null)
        {
            if(optionService.updateOption(option) != 0)
            {
                return Result.success(optionService.allOptionInQuestion(option.getMultiple_choice_question_id()));
            }
            else
            {
                return Result.error("更新失败");
            }
        }
        else
        {
            return Result.error("选项" + option.getOption_id() + "不存在");
        }
    }

    @PostMapping("/options/add")
    public Result addOption(@RequestBody MultipleChoiceOption option)
    {
        if (option == null || option.getMultiple_choice_question_id() == null) {
            return Result.error("请求数据不合法");
        }
        if (mcQuestionService.selectQuestionById(option.getMultiple_choice_question_id()) == null) {
            return Result.error("选择题" + option.getMultiple_choice_question_id() + "不存在");
        }
        if (option.getOption_id() != null && option.getOption_id() > 0 && optionService.selectOptionById(option.getOption_id()) != null) {
            return Result.error("选项" + option.getOption_id() + "已存在");
        }
        if (option.getOrder_in_question() == null) {
            option.setOrder_in_question(optionService.allOptionInQuestion(option.getMultiple_choice_question_id()).size() + 1);
        }
        option.setOption_id(null);
        if (optionService.addOption(option) != 0) {
            return Result.success(option);
        }
        return Result.error("添加失败");
    }



    // 填空题相关

    @GetMapping("/questions/fi")
    public Result listFIQuestions()
    {
        return Result.success(fiQuestionService.listAllQuestions());
    }

    @GetMapping("/questions/fi/in_paper/{id}")
    public Result selectAllFIQuestionsInPaper(@PathVariable int id)
    {
        return Result.success(fiQuestionService.listAllQuestionsInPaper(id));
    }


    @GetMapping("/questions/fi/search/des/{keyword}")
    public Result searchFIQuestionsDes(@PathVariable String keyword)
    {
        return Result.success(fiQuestionService.searchQuestionsByDescription(keyword));
    }

    @GetMapping("/questions/fi/search/anal/{keyword}")
    public Result searchFIQuestionsAnal(@PathVariable String keyword)
    {
        return Result.success(fiQuestionService.searchQuestionByAnalysis(keyword));
    }

    @GetMapping("/questions/fi/search/keyword/{keyword}")
    public Result searchFIQuestionsKeyword(@PathVariable String keyword)
    {
        return Result.success(fiQuestionService.searchQuestionByKeyword(keyword));
    }

    @GetMapping("/questions/fi/delete/{id}")
    public Result deleteFIQuestion(@PathVariable int id)
    {
        if(fiQuestionService.selectQuestionById(id) == null)
        {
            return Result.error("题目" + id + "不存在");
        }
        else
        {
            if(fiQuestionService.deleteQuestion(id) != 0)
            {
                return Result.success("删除成功");

            }
            else
            {
                return Result.error("删除失败");
            }
        }
    }


    @PostMapping("/questions/fi/add")
    public Result addFIQuestion(@RequestBody FillInTheBlankQuestion question)
    {
        if (question == null || StrUtil.isBlank(question.getQuestion_description())) {
            return Result.error("题干不能为空");
        }
        if (StrUtil.isBlank(question.getCorrect_answer())) {
            return Result.error("请填写正确答案");
        }
        if (fiQuestionService.selectQuestionByDes(question.getQuestion_description().trim()) != null) {
            return Result.error("题目已存在：" + question.getQuestion_description());
        }
        question.setQuestion_description(question.getQuestion_description().trim());
        if (fiQuestionService.addQuestion(question) == 0) {
            return Result.error("添加失败");
        }
        if (question.getFill_in_the_blank_question_id() == null) {
            FillInTheBlankQuestion saved = fiQuestionService.selectQuestionByDes(question.getQuestion_description());
            return saved != null ? Result.success(saved) : Result.success(question);
        }
        return Result.success(question);
    }

    @PostMapping("/questions/fi/add/keyword")
    public Result addKeywordInFIQuestion(@RequestBody KeywordFillInTheBlankQuestion keywordFillInTheBlankQuestion)
    {
        if(fiQuestionService.selectQuestionById(keywordFillInTheBlankQuestion.getFill_in_the_blank_question_id()) == null)
        {
            return Result.error("题目" + keywordFillInTheBlankQuestion.getFill_in_the_blank_question_id() + "不存在");
        }
        else
        {
            if(keywordService.selectKeywordById(keywordFillInTheBlankQuestion.getKeyword_id()) == null)
            {
                return Result.error("关键词" + keywordFillInTheBlankQuestion.getKeyword_id() + "不存在");
            }
            else
            {
                if(keywordFIQuestionService.addKeywordFIQuestion(keywordFillInTheBlankQuestion) != 0)
                {
                    return Result.success(keywordFIQuestionService.selectKeywordFIQuestionByQuestionId(keywordFillInTheBlankQuestion.getFill_in_the_blank_question_id()));
                }
                else
                {
                    return Result.error("添加失败");
                }
            }
        }
    }

    @PostMapping("/questions/fi/delete/keyword")
    public Result deleteKeywordInFIQuestion(@RequestBody KeywordFillInTheBlankQuestion keywordFillInTheBlankQuestion)
    {
        if(fiQuestionService.selectQuestionById(keywordFillInTheBlankQuestion.getFill_in_the_blank_question_id()) == null)
        {
            return Result.error("题目" + keywordFillInTheBlankQuestion.getFill_in_the_blank_question_id() + "不存在");
        }
        else
        {
            if(keywordService.selectKeywordById(keywordFillInTheBlankQuestion.getKeyword_id()) == null)
            {
                return Result.error("关键词" + keywordFillInTheBlankQuestion.getKeyword_id() + "不存在");
            }
            else
            {
                if(keywordFIQuestionService.deleteKeywordFIQuestion(keywordFillInTheBlankQuestion) != 0)
                {
                    return Result.success(keywordFIQuestionService.selectKeywordFIQuestionByQuestionId(keywordFillInTheBlankQuestion.getFill_in_the_blank_question_id()));
                }
                else
                {
                    return Result.error("删除失败");
                }
            }
        }
    }


    @PostMapping("/questions/fi/update")
    public Result updateFIQuestion(@RequestBody FillInTheBlankQuestion question)
    {
        if(fiQuestionService.updateQuestion(question) != 0)
        {
            return Result.success("更新成功");
        }
        else
        {
            return Result.error("更新失败");
        }
    }



}
