package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.FillInTheBlankQuestion;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FIQuestionMapper extends BaseMapper<FillInTheBlankQuestion> {
    String select_all_FIQuestions =
            "SELECT fill_in_the_blank_question_id, question_description, correct_answer, question_analysis " +
            "FROM fillintheblankquestion_o " +
            "ORDER BY fill_in_the_blank_question_id ";
    @Select(select_all_FIQuestions)
    List<FillInTheBlankQuestion> selectAllFIQuestions();

    String select_all_FIQuestions_in_paper_sql =
            "SELECT fillintheblankquestion_o.fill_in_the_blank_question_id, question_description, correct_answer, question_analysis, paperfillintheblankquestion_o.question_order_in_paper " +
            "FROM fillintheblankquestion_o JOIN paperfillintheblankquestion_o on fillintheblankquestion_o.fill_in_the_blank_question_id = paperfillintheblankquestion_o.fill_in_the_blank_question_id " +
            "WHERE test_paper_id = #{id}";
    @Select(select_all_FIQuestions_in_paper_sql)
    List<FillInTheBlankQuestion> selectAllFIQuestionsInPaper(int id);


    String select_FIQuestion_byId_sql =
            "SELECT fill_in_the_blank_question_id, question_description, correct_answer, question_analysis " +
            "FROM fillintheblankquestion_o " +
            "WHERE fill_in_the_blank_question_id = #{id}";
    @Select(select_FIQuestion_byId_sql)
    FillInTheBlankQuestion selectFIQuestionById(int id);

    String select_FIQuestion_byDes =
            "SELECT fill_in_the_blank_question_id, question_description, correct_answer, question_analysis " +
            "FROM fillintheblankquestion_o " +
            "WHERE question_description = #{description}";
    @Select(select_FIQuestion_byDes)
    FillInTheBlankQuestion selectFIQuestionByDes(String description);


    String update_FIQuestion_sql =
            "UPDATE fillintheblankquestion_o " +
            "SET fill_in_the_blank_question_id = #{fill_in_the_blank_question_id}, question_description = #{question_description}, " +
            "correct_answer = #{correct_answer}, question_analysis = #{question_analysis} " +
            "WHERE fill_in_the_blank_question_id = #{fill_in_the_blank_question_id}";
    @Update(update_FIQuestion_sql)
    int updateFIQuestion(FillInTheBlankQuestion question);

    String add_FIQuestion_sql =
            "INSERT INTO fillintheblankquestion_o(question_description, correct_answer, question_analysis) " +
            "VALUES (#{question_description}, #{correct_answer}, #{question_analysis})";
    @Insert(add_FIQuestion_sql)
    @Options(useGeneratedKeys = true, keyProperty = "fill_in_the_blank_question_id", keyColumn = "fill_in_the_blank_question_id")
    int addFIQuestion(FillInTheBlankQuestion question);

    String delete_FIQuestion_byId_sql =
            "DELETE FROM fillintheblankquestion_o WHERE fill_in_the_blank_question_id = #{id}";
    @Delete(delete_FIQuestion_byId_sql)
    int deleteFIQuestionById(int id);

    String search_FIQuestion_description_byKeyword =
            "SELECT fill_in_the_blank_question_id, question_description, correct_answer, question_analysis " +
            "FROM fillintheblankquestion_o " +
            "WHERE question_description LIKE #{keyword}";
    @Select(search_FIQuestion_description_byKeyword)
    List<FillInTheBlankQuestion> searchFIQuestionsDescription(String keyword);

    String search_FIQuestion_analysis_byKeyword =
            "SELECT fill_in_the_blank_question_id, question_description, correct_answer, question_analysis " +
            "FROM fillintheblankquestion_o " +
            "WHERE question_analysis LIKE #{keyword}";
    @Select(search_FIQuestion_analysis_byKeyword)
    List<FillInTheBlankQuestion> searchFIQuestionsAnalysis(String keyword);

    String search_FIQuestion_byKeyword_sql =
            "SELECT keywordfillintheblankquestion_o.fill_in_the_blank_question_id, question_description, correct_answer, question_analysis " +
            "FROM fillintheblankquestion_o " +
            "JOIN keywordfillintheblankquestion_o ON fillintheblankquestion_o.fill_in_the_blank_question_id = keywordfillintheblankquestion_o.fill_in_the_blank_question_id " +
            "JOIN keyword_o ON keywordfillintheblankquestion_o.keyword_id = keyword_o.keyword_id " +
            "WHERE keyword_o.keyword_description LIKE #{keyword}";
    @Select(search_FIQuestion_byKeyword_sql)
    List<FillInTheBlankQuestion> searchFIQuestionsKeyword(String keyword);


}
