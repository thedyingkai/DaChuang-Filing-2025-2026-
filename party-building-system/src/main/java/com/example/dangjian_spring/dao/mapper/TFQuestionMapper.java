package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.TrueFalseQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TFQuestionMapper extends BaseMapper<TrueFalseQuestion>{
    String select_all_TFQuestions =
            "SELECT true_false_question_id, question_description, correct_answer, question_analysis " +
            "FROM truefalsequestion_o " +
            "ORDER BY true_false_question_id ";
    @Select(select_all_TFQuestions)
    List<TrueFalseQuestion> selectAllTFQuestions();

    String select_all_TFQuestions_in_paper_sql =
            "SELECT truefalsequestion_o.true_false_question_id, question_description, correct_answer, papertruefalsequestion_o.question_order_in_paper " +
            "FROM truefalsequestion_o JOIN papertruefalsequestion_o ON truefalsequestion_o.true_false_question_id = papertruefalsequestion_o.true_false_question_id " +
            "WHERE papertruefalsequestion_o.test_paper_id = #{id}";
    @Select(select_all_TFQuestions_in_paper_sql)
    List<TrueFalseQuestion> selectAllTFQuestionsInPaper(int id);

    String select_TFQuestion_byId_sql =
            "SELECT true_false_question_id, question_description, correct_answer, question_analysis " +
            "FROM truefalsequestion_o " +
            "WHERE true_false_question_id = #{id}";
    @Select(select_TFQuestion_byId_sql)
    TrueFalseQuestion selectTFQuestionById(int id);

    String select_TFQuestion_byDes =
            "SELECT true_false_question_id, question_description, correct_answer, question_analysis " +
            "FROM truefalsequestion_o " +
            "WHERE question_description = #{description}";
    @Select(select_TFQuestion_byDes)
    TrueFalseQuestion selectTFQuestionByDes(String description);


    String update_TFQuestion_sql =
            "UPDATE truefalsequestion_o " +
            "SET true_false_question_id = #{true_false_question_id}, question_description = #{question_description}, " +
            "correct_answer = #{correct_answer}, question_analysis = #{question_analysis} " +
            "WHERE true_false_question_id = #{true_false_question_id}";
    @Update(update_TFQuestion_sql)
    int updateTFQuestion(TrueFalseQuestion question);

    String add_TFQuestion_sql =
            "INSERT INTO truefalsequestion_o(question_description, correct_answer, question_analysis) " +
            "VALUES (#{question_description}, #{correct_answer}, #{question_analysis})";
    @Insert(add_TFQuestion_sql)
    @Options(useGeneratedKeys = true, keyProperty = "true_false_question_id", keyColumn = "true_false_question_id")
    int addTFQuestion(TrueFalseQuestion question);

    String delete_TFQuestion_byId_sql =
            "DELETE FROM keywordtruefalsequestion_o where keywordtruefalsequestion_o.true_false_question_id = #{id};" +
            "DELETE FROM truefalsequestion_o WHERE true_false_question_id = #{id};";
    @Delete(delete_TFQuestion_byId_sql)
    int deleteTFQuestionById(int id);

    String search_TFQuestion_description_byKeyword =
            "SELECT true_false_question_id, question_description, correct_answer, question_analysis " +
            "FROM truefalsequestion_o " +
            "WHERE question_description LIKE #{keyword}";
    @Select(search_TFQuestion_description_byKeyword)
    List<TrueFalseQuestion> searchTFQuestionsDescription(String keyword);

    String search_TFQuestion_analysis_byKeyword =
            "SELECT true_false_question_id, question_description, correct_answer, question_analysis " +
            "FROM truefalsequestion_o " +
            "WHERE question_analysis LIKE #{keyword}";
    @Select(search_TFQuestion_analysis_byKeyword)
    List<TrueFalseQuestion> searchTFQuestionsAnalysis(String keyword);

    String search_TFQuestion_byKeyword_sql =
            "SELECT truefalsequestion_o.true_false_question_id, question_description, correct_answer, question_analysis " +
            "FROM truefalsequestion_o " +
            "JOIN keywordtruefalsequestion_o ON truefalsequestion_o.true_false_question_id = keywordtruefalsequestion_o.true_false_question_id " +
            "JOIN keyword_o ON keywordtruefalsequestion_o.keyword_id = keyword_o.keyword_id " +
            "WHERE keyword_o.keyword_description LIKE #{keyword}";
    @Select(search_TFQuestion_byKeyword_sql)
    List<TrueFalseQuestion> searchTFQuestionsKeyword(String keyword);


}
