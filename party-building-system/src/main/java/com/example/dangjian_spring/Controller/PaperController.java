package com.example.dangjian_spring.Controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.FillInTheBlankQuestion;
import com.example.dangjian_spring.entity.TrueFalseQuestion;
import com.example.dangjian_spring.entity.MultipleChoiceQuestion;
import com.example.dangjian_spring.entity.MultipleChoiceOption;
import com.example.dangjian_spring.entity.TestPaper;
import com.example.dangjian_spring.entity.PaperTrueFalseQuestion;
import com.example.dangjian_spring.entity.PaperMultipleChoiceQuestion;
import com.example.dangjian_spring.entity.PaperFillInTheBlankQuestion;
import com.example.dangjian_spring.service.FIQuestionService;
import com.example.dangjian_spring.service.TFQuestionService;
import com.example.dangjian_spring.service.MCQuestionService;
import com.example.dangjian_spring.service.OptionService;
import com.example.dangjian_spring.service.PaperService;
import com.example.dangjian_spring.service.PaperTFQuestionService;
import com.example.dangjian_spring.service.PaperMCQuestionService;
import com.example.dangjian_spring.service.PaperFIQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
public class PaperController {
    @Resource
    PaperService paperService;
    @Resource
    PaperTFQuestionService paperTFQuestionService;
    @Resource
    PaperMCQuestionService paperMCQuestionService;
    @Resource
    TFQuestionService tfQuestionService;
    @Resource
    MCQuestionService mcQuestionService;
    @Resource
    FIQuestionService fiQuestionService;
    @Autowired
    private PaperFIQuestionService paperFIQuestionService;

    @GetMapping("/papers")
    public Result allPapers()
    {
        return Result.success(paperService.allPapers());
    }

    /** 在线自测提交参与记录时使用的占位试卷（无题目，仅存 participation） */
    @GetMapping("/papers/self-exam")
    public Result selfExamPaper() {
        return Result.success(paperService.getOrCreateSelfExamPaper());
    }

    @GetMapping("/papers/delete/{id}")
    public Result deletePaper(@PathVariable int id)
    {
        TestPaper target = paperService.selectPaperById(id);
        if(target == null)
        {
            return Result.error("试卷" + id + "不存在");
        }
        if (PaperService.SELF_EXAM_PAPER_DESCRIPTION.equals(target.getPaper_description())) {
            return Result.error("系统占位试卷「在线自测」不可删除");
        }
        else
        {
            if(paperService.deletePaper(id) != 0)
            {
                return Result.success("删除成功");
            }
            else
            {
                return Result.error("删除失败");
            }
        }
    }

    @GetMapping("/papers/search/description/{keyword}")
    public Result searchPapersByDes(@PathVariable String keyword)
    {
        return Result.success(paperService.searchPapersByDes(keyword));
    }

    @GetMapping("/papers/search/method/{keyword}")
    public Result searchPapersByMethod(@PathVariable String keyword)
    {
        return Result.success(paperService.searchPaperByMethod(keyword));
    }

    @PostMapping("/papers/create")
    public Result addPaper(@RequestBody TestPaper paper) {
        if (paper == null) {
            return Result.error("请求体不能为空");
        }
        if (StrUtil.isBlank(paper.getPaper_description())) {
            return Result.error("请填写试卷标题");
        }
        paper.setPaper_description(paper.getPaper_description().trim());
        if (StrUtil.isBlank(paper.getGrouping_method())) {
            return Result.error("请填写组卷方式");
        }
        if (paper.getPoints_reward() == null) {
            paper.setPoints_reward(0);
        }
        if (StrUtil.isBlank(paper.getCreate_date())) {
            paper.setCreate_date(DateUtil.now());
        }
        if (StrUtil.isBlank(paper.getDeadline())) {
            return Result.error("请填写截止时间");
        }
        if (paperService.selectPaperByDes(paper.getPaper_description()) != null) {
            return Result.error("试卷标题已存在：" + paper.getPaper_description());
        }
        if (paperService.addPaper(paper) == 0) {
            return Result.error("添加失败");
        }
        // 部分环境下 useGeneratedKeys 未回填主键，再查一次保证返回含 test_paper_id
        if (paper.getTest_paper_id() == null) {
            TestPaper saved = paperService.selectPaperByDes(paper.getPaper_description());
            if (saved != null) {
                paper.setTest_paper_id(saved.getTest_paper_id());
            }
        }
        if (paper.getTest_paper_id() == null) {
            return Result.error("添加失败：未生成试卷编号，请检查数据库 TestPaper 主键是否为自增");
        }
        return Result.success(paper);
    }

    @PostMapping("/papers/update")
    public Result updatePaper(@RequestBody TestPaper paper)
    {
        if(paperService.selectPaperById(paper.getTest_paper_id()) == null)
        {
            return Result.error("试卷" + paper.getTest_paper_id() + "不存在");
        }
        else
        {
            if(paperService.updatePaper(paper) != 0)
            {
                return Result.success(paperService.allPapers());
            }
            else
            {
                return Result.error("更新失败");
            }
        }
    }

    @PostMapping("/papers/add/tf")
    public Result addTFQuestionInPaper(@RequestBody PaperTrueFalseQuestion paperTrueFalseQuestion)
    {
        if(paperService.selectPaperById(paperTrueFalseQuestion.getTest_paper_id()) == null)
        {
            return Result.error("试卷" + paperTrueFalseQuestion.getTest_paper_id() + "不存在");
        }
        else
        {
            if (tfQuestionService.selectQuestionById(paperTrueFalseQuestion.getTrue_false_question_id()) == null)
            {
                return Result.error("判断题" + paperTrueFalseQuestion.getTrue_false_question_id() + "不存在");
            }
            else
            {
                if(paperTFQuestionService.addTFQuestionInPaper(paperTrueFalseQuestion) != 0)
                {
                    return Result.success(paperTFQuestionService.allTFQuestionsInPaper(paperTrueFalseQuestion.getTest_paper_id()));
                }
                else
                {
                    return Result.error("添加失败");
                }
            }
        }
    }

    @PostMapping("/papers/add/mc")
    public Result addMCQuestionInPaper(@RequestBody PaperMultipleChoiceQuestion paperMultipleChoiceQuestion)
    {
        if(paperService.selectPaperById(paperMultipleChoiceQuestion.getTest_paper_id()) == null)
        {
            return Result.error("试卷" + paperMultipleChoiceQuestion.getTest_paper_id() + "不存在");
        }
        else
        {
            if (mcQuestionService.selectQuestionById(paperMultipleChoiceQuestion.getMultiple_choice_question_id()) == null)
            {
                return Result.error("选择题" + paperMultipleChoiceQuestion.getMultiple_choice_question_id() + "不存在");
            }
            else
            {
                if(paperMCQuestionService.addMCQuestionInPaper(paperMultipleChoiceQuestion) != 0)
                {
                    return Result.success(paperMCQuestionService.allMCQuestionsInPaper(paperMultipleChoiceQuestion.getTest_paper_id()));
                }
                else
                {
                    return Result.error("添加失败");
                }
            }
        }
    }

    @PostMapping("/papers/add/fi")
    public Result addFIQuestionInPaper(@RequestBody PaperFillInTheBlankQuestion paperFillInTheBlankQuestion)
    {
        if(paperService.selectPaperById(paperFillInTheBlankQuestion.getTest_paper_id()) == null)
        {
            return Result.error("试卷" + paperFillInTheBlankQuestion.getTest_paper_id() + "不存在");
        }
        else
        {
            if (fiQuestionService.selectQuestionById(paperFillInTheBlankQuestion.getFill_in_the_blank_question_id()) == null)
            {
                return Result.error("填空题" + paperFillInTheBlankQuestion.getFill_in_the_blank_question_id() + "不存在");
            }
            else
            {
                if(paperFIQuestionService.addFIQuestionInPaper(paperFillInTheBlankQuestion) != 0)
                {
                    return Result.success(paperFIQuestionService.allFIQuestionsInPaper(paperFillInTheBlankQuestion.getTest_paper_id()));
                }
                else
                {
                    return Result.error("添加失败");
                }
            }
        }
    }

    @PostMapping("/papers/remove/tf")
    public Result deleteTFQuestionInPaper(@RequestBody PaperTrueFalseQuestion paperTrueFalseQuestion)
    {
            if(paperService.selectPaperById(paperTrueFalseQuestion.getTest_paper_id()) == null)
            {
                return Result.error("试卷" + paperTrueFalseQuestion.getTest_paper_id() + "不存在");
            }
            else
            {
                if (tfQuestionService.selectQuestionById(paperTrueFalseQuestion.getTrue_false_question_id()) == null)
                {
                    return Result.error("判断题" + paperTrueFalseQuestion.getTrue_false_question_id() + "不存在");
                }
                else
                {
                    if(paperTFQuestionService.deleteTFQuestionInPaper(paperTrueFalseQuestion) != 0)
                    {
                        return Result.success(paperTFQuestionService.allTFQuestionsInPaper(paperTrueFalseQuestion.getTest_paper_id()));
                    }
                    else
                    {
                        return Result.error("删除失败");
                    }
                }
            }
    }

    @PostMapping("/papers/remove/mc")
    public Result deleteTFQuestionInPaper(@RequestBody PaperMultipleChoiceQuestion paperMultipleChoiceQuestion)
    {
            if(paperService.selectPaperById(paperMultipleChoiceQuestion.getTest_paper_id()) == null)
            {
                return Result.error("试卷" + paperMultipleChoiceQuestion.getTest_paper_id() + "不存在");
            }
            else
            {
                if (mcQuestionService.selectQuestionById(paperMultipleChoiceQuestion.getMultiple_choice_question_id()) == null)
                {
                    return Result.error("选择题" + paperMultipleChoiceQuestion.getMultiple_choice_question_id() + "不存在");
                }
                else
                {
                    if(paperMCQuestionService.deleteMCQuestionInPaper(paperMultipleChoiceQuestion) != 0)
                    {
                        return Result.success(paperMCQuestionService.allMCQuestionsInPaper(paperMultipleChoiceQuestion.getTest_paper_id()));
                    }
                    else
                    {
                        return Result.error("删除失败");
                    }
                }
            }
    }

    @PostMapping("/papers/remove/fi")
    public Result deleteFIQuestionInPaper(@RequestBody PaperFillInTheBlankQuestion paperFillInTheBlankQuestion)
    {
        if(paperService.selectPaperById(paperFillInTheBlankQuestion.getTest_paper_id()) == null)
        {
            return Result.error("试卷" + paperFillInTheBlankQuestion.getTest_paper_id() + "不存在");
        }
        else
        {
            if (fiQuestionService.selectQuestionById(paperFillInTheBlankQuestion.getFill_in_the_blank_question_id()) == null)
            {
                return Result.error("填空题" + paperFillInTheBlankQuestion.getFill_in_the_blank_question_id() + "不存在");
            }
            else
            {
                if(paperFIQuestionService.deleteFIQuestionInPaper(paperFillInTheBlankQuestion) != 0)
                {
                    return Result.success(paperFIQuestionService.allFIQuestionsInPaper(paperFillInTheBlankQuestion.getTest_paper_id()));
                }
                else
                {
                    return Result.error("删除失败");
                }
            }
        }
    }











}
