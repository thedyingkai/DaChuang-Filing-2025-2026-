package com.example.dangjian_spring.Controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.service.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.dangjian_spring.entity.Keyword;
import com.example.dangjian_spring.entity.FillInTheBlankQuestion;
import com.example.dangjian_spring.entity.MultipleChoiceQuestion;
import com.example.dangjian_spring.entity.MultipleChoiceOption;
import com.example.dangjian_spring.entity.TrueFalseQuestion;
import com.example.dangjian_spring.entity.KeywordFillInTheBlankQuestion;
import com.example.dangjian_spring.entity.KeywordMultipleChoiceQuestion;
import com.example.dangjian_spring.entity.KeywordTrueFalseQuestion;


import java.util.List;

@RestController
@Slf4j

public class BatchController {

    @Resource
    KeywordService keywordService = new KeywordService();
    @Resource
    FIQuestionService fiQuestionService = new FIQuestionService();
    @Resource
    MCQuestionService mcQuestionService = new MCQuestionService();
    @Resource
    OptionService optionService = new OptionService();
    @Resource
    TFQuestionService tfQuestionService = new TFQuestionService();
    @Resource
    KeywordFIQuestionService keywordFIQuestionService = new KeywordFIQuestionService();
    @Resource
    KeywordMCQuestionService keywordMCQuestionService = new KeywordMCQuestionService();
    @Resource
    KeywordTFQuestionService keywordTFQuestionService = new KeywordTFQuestionService();


    @PostMapping("/batch/import")
    public Result importBatch(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("未获取到上传文件");
        }
        else
        {
            String name = file.getOriginalFilename();
            String mainName = FileUtil.mainName(name);
            String extName = FileUtil.extName(name);

            InputStream fis = file.getInputStream();

            if(extName == null){
                return Result.error("文件后缀名丢失");
            }

            if(extName.equals("xsl") || extName.equals("xlsx")){
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0);
                Row first_row = sheet.getRow(0);

                int row_index = first_row.getRowNum() + 1;
                for(int i = row_index; i <= sheet.getLastRowNum(); i++) {

                    Row r = sheet.getRow(i);

                    if (r.getLastCellNum() < 4) {
                        log.debug("第" + i + "行格式不正确");
                        continue;
                    }

                    Cell c = r.getCell(0);
                    String type;
                    if (c.getCellType() == CellType.STRING) {
                        type = c.getStringCellValue();
                        log.debug("type->" + type + "\t");
                    } else {
                        log.debug("第" + i + "行" + 0 + "列为非字符串类型");
                        continue;
                    }

                    c = r.getCell(1);
                    String keyword;
                    if (c.getCellType() == CellType.STRING) {
                        keyword = c.getStringCellValue();
                        log.debug("keyword->" + keyword + "\t");
                    } else {
                        log.debug("第" + i + "行" + 1 + "列为非字符串类型");
                        continue;
                    }

                    c = r.getCell(2);
                    String content;
                    if (c.getCellType() == CellType.STRING) {
                        content = c.getStringCellValue();
                        log.debug("content->" + content);
                    } else {
                        log.debug("第" + i + "行" + 2 + "列为非字符串类型");
                        continue;
                    }

                    c = r.getCell(3);
                    String analysis;
                    if (c.getCellType() == CellType.STRING) {
                        analysis = c.getStringCellValue();
                        log.debug("analysis->" + analysis);
                    } else {
                        log.debug("第" + i + "行" + 3 + "列为非字符串类型");
                        continue;
                    }

                    c = r.getCell(4);
                    String answer;
                    if (c.getCellType() == CellType.STRING) {
                        answer = c.getStringCellValue();
                        log.debug("answer->" + analysis);
                    } else {
                        log.debug("第" + i + "行" + 3 + "列为非字符串类型");
                        continue;
                    }

                    List<String> optionList = new ArrayList<>();

                    for (int x = 5; x < r.getLastCellNum(); x++) {
                        Cell cell = r.getCell(x);
                        if (cell.getCellType() == CellType.STRING) {
                            optionList.add(cell.getStringCellValue());
                        } else {
                            log.debug("第" + i + "行" + x + "列为非字符串类型");
                        }
                    }

                    Keyword k = keywordService.selectKeywordByDes(keyword);
                    int keyword_id;
                    if(k != null)
                    {
                        keyword_id = k.getKeyword_id();

                    }
                    else
                    {
                        Keyword key_word = new Keyword();
                        key_word.setKeyword_description(keyword);
                        key_word.setColumn_id(0);
                        key_word.setKeyword_id(keywordService.allKeywords().size() + 1);
                        if(keywordService.addKeyword(key_word) != 0)
                        {
                            log.debug("关键词添加成功");
                            keyword_id = key_word.getKeyword_id();
                        }
                        else
                        {
                            log.debug("关键词添加失败");
                            continue;
                        }
                    }

                    switch (type)
                    {
                        case "判断":

                            TrueFalseQuestion trueFalseQuestion = new TrueFalseQuestion();
                            trueFalseQuestion.setCorrect_answer(answer);
                            trueFalseQuestion.setQuestion_analysis(analysis);
                            trueFalseQuestion.setQuestion_description(content);
                            trueFalseQuestion.setTrue_false_question_id(tfQuestionService.listAllQuestions().size() + 1);

                            if(tfQuestionService.addQuestion(trueFalseQuestion) != 0)
                            {
                                KeywordTrueFalseQuestion keywordTrueFalseQuestion = new KeywordTrueFalseQuestion();
                                keywordTrueFalseQuestion.setKeyword_id(keyword_id);
                                keywordTrueFalseQuestion.setTrue_false_question_id(trueFalseQuestion.getTrue_false_question_id());

                                keywordTFQuestionService.addKeywordTFQuestion(keywordTrueFalseQuestion);

                            }
                            break;

                        case "填空":

                            FillInTheBlankQuestion fillInTheBlankQuestion = new FillInTheBlankQuestion();
                            fillInTheBlankQuestion.setQuestion_description(content);
                            fillInTheBlankQuestion.setQuestion_analysis(analysis);
                            fillInTheBlankQuestion.setCorrect_answer(answer);
                            fillInTheBlankQuestion.setFill_in_the_blank_question_id(fiQuestionService.listAllQuestions().size() + 1);

                            if(fiQuestionService.addQuestion(fillInTheBlankQuestion) != 0)
                            {
                                KeywordFillInTheBlankQuestion keywordFillInTheBlankQuestion = new KeywordFillInTheBlankQuestion();
                                keywordFillInTheBlankQuestion.setFill_in_the_blank_question_id(fillInTheBlankQuestion.getFill_in_the_blank_question_id());
                                keywordFillInTheBlankQuestion.setKeyword_id(keyword_id);

                                keywordFIQuestionService.addKeywordFIQuestion(keywordFillInTheBlankQuestion);
                            }
                            break;

                        case "选择":

                            if(optionList.contains(answer))
                            {
                                int answer_id = optionList.indexOf(answer) + 1;
                                MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
                                multipleChoiceQuestion.setMultiple_choice_question_id(mcQuestionService.listAllQuestions().size() + 1);
                                multipleChoiceQuestion.setQuestion_analysis(analysis);
                                multipleChoiceQuestion.setCorrect_answer(answer_id);
                                multipleChoiceQuestion.setQuestion_description(content);

                                if(mcQuestionService.addQuestion(multipleChoiceQuestion) != 0)
                                {
                                    for(int x = 0; x < optionList.size(); x++)
                                    {
                                        MultipleChoiceOption multipleChoiceOption = new MultipleChoiceOption();

                                        multipleChoiceOption.setOrder_in_question(x + 1);
                                        multipleChoiceOption.set_correct(x + 1 == answer_id);
                                        multipleChoiceOption.setMultiple_choice_question_id(multipleChoiceQuestion.getMultiple_choice_question_id());
                                        multipleChoiceOption.setOption_id(optionService.allOptions().size() + 1);
                                        multipleChoiceOption.setOption_description(optionList.get(x));

                                        optionService.addOption(multipleChoiceOption);
                                    }

                                    KeywordMultipleChoiceQuestion keywordMultipleChoiceQuestion = new KeywordMultipleChoiceQuestion();
                                    keywordMultipleChoiceQuestion.setKeyword_id(keyword_id);
                                    keywordMultipleChoiceQuestion.setMultiple_choice_question_id(multipleChoiceQuestion.getMultiple_choice_question_id());

                                    keywordMCQuestionService.addKeywordMCQuestion(keywordMultipleChoiceQuestion);

                                }

                            }

                            break;
                    }



                }

                return Result.success("上传读取成功");
            }
            else {
                return Result.error("上传文件格式错误");
            }
        }

    }

    @GetMapping("/batch/export")
    public void exportBatch(HttpServletResponse response) throws IOException {

        int count = 1;
        Date date = new Date(); // this object contains the current date value
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String file_name = "batch-" + formatter.format(date) + ".xlsx";

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + file_name);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("第一页");
        XSSFRow first_row = sheet.createRow(0);

        first_row.createCell(0).setCellValue("题型");
        first_row.createCell(1).setCellValue("关键词");
        first_row.createCell(2).setCellValue("题干");
        first_row.createCell(3).setCellValue("解析");
        first_row.createCell(4).setCellValue("正确答案");

        List<TrueFalseQuestion> trueFalseQuestionList = tfQuestionService.listAllQuestions();

        if (!trueFalseQuestionList.isEmpty())
        {
            for(TrueFalseQuestion tf : trueFalseQuestionList)
            {
                int id = tf.getTrue_false_question_id();
                List<Keyword> keywordList = keywordService.selectKeywordsInTFQuestion(id);
                Keyword keyword = new Keyword();

                if(keywordList.isEmpty())
                {
                    keyword.setKeyword_description("无");
                }
                else {
                    keyword = keywordList.get(0);
                }

                XSSFRow row = sheet.createRow(count);
                row.createCell(0).setCellValue("判断");
                row.createCell(1).setCellValue(keyword.getKeyword_description());
                row.createCell(2).setCellValue(tf.getQuestion_description());
                row.createCell(3).setCellValue(tf.getQuestion_analysis());
                row.createCell(4).setCellValue(tf.getCorrect_answer());

                count = count + 1;
            }
        }

        List<MultipleChoiceQuestion> multipleChoiceQuestionList = mcQuestionService.listAllQuestions();

        if(!multipleChoiceQuestionList.isEmpty())
        {
            for(MultipleChoiceQuestion mc : multipleChoiceQuestionList)
            {
                int id = mc.getMultiple_choice_question_id();
                List<MultipleChoiceOption> multipleChoiceOptionList = optionService.allOptionInQuestion(id);
                List<Keyword> keywordList = keywordService.selectKeywordInMCQuestion(id);
                Keyword keyword = new Keyword();

                if(keywordList.isEmpty())
                {
                    keyword.setKeyword_description("无");
                }
                else {
                    keyword = keywordList.get(0);
                }

                XSSFRow row = sheet.createRow(count);
                row.createCell(0).setCellValue("选择");
                row.createCell(1).setCellValue(keyword.getKeyword_description());
                row.createCell(2).setCellValue(mc.getQuestion_description());
                row.createCell(3).setCellValue(mc.getQuestion_analysis());

                String answer = "";

                int x = 5;
                for(MultipleChoiceOption option : multipleChoiceOptionList)
                {
                    row.createCell(x).setCellValue(option.getOption_description());
                    if(mc.getCorrect_answer() == option.getOrder_in_question())
                    {
                        answer = option.getOption_description();
                    }
                    x = x + 1;
                }

                row.createCell(4).setCellValue(answer);

                count = count + 1;

            }
        }

        List<FillInTheBlankQuestion> fillInTheBlankQuestionList = fiQuestionService.listAllQuestions();

        if(!fillInTheBlankQuestionList.isEmpty())
        {
            for(FillInTheBlankQuestion fi : fillInTheBlankQuestionList)
            {
                int id = fi.getFill_in_the_blank_question_id();
                List<Keyword> keywordList = keywordService.selectKeywordInFIQuestion(id);
                Keyword keyword = new Keyword();

                if(keywordList.isEmpty())
                {
                    keyword.setKeyword_description("无");
                }
                else {
                    keyword = keywordList.get(0);
                }

                XSSFRow row = sheet.createRow(count);
                row.createCell(0).setCellValue("填空");
                row.createCell(1).setCellValue(keyword.getKeyword_description());
                row.createCell(2).setCellValue(fi.getQuestion_description());
                row.createCell(3).setCellValue(fi.getQuestion_analysis());
                row.createCell(4).setCellValue(fi.getCorrect_answer());

                count = count + 1;
            }
        }

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
