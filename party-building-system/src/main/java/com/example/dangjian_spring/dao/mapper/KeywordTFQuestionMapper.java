package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.KeywordTrueFalseQuestion;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KeywordTFQuestionMapper extends BaseMapper<KeywordTrueFalseQuestion> {
    String add_keyword_in_TFQuestion_sql =
            "INSERT INTO keywordtruefalsequestion_o(keyword_id,true_false_question_id) " +
            "VALUES (#{keyword_id}, #{true_false_question_id})";
    @Insert(add_keyword_in_TFQuestion_sql)
    int addKeywordInTFQuestion(KeywordTrueFalseQuestion keywordTrueFalseQuestion);

    String delete_keyword_in_TFQuestion_sql =
            "DELETE FROM keywordtruefalsequestion_o WHERE keyword_id = #{keyword_id} AND true_false_question_id = #{true_false_question_id}";
    @Delete(delete_keyword_in_TFQuestion_sql)
    int deleteKeywordTFQuestion(KeywordTrueFalseQuestion keywordTrueFalseQuestion);

    String delete_keyword_by_TFQuestion_sql =
            "DELETE FROM keywordtruefalsequestion_o WHERE true_false_question_id = #{id}";
    @Delete(delete_keyword_by_TFQuestion_sql)
    int deleteKeywordByTFQuestionId(int id);

    String select_keyword_TFQuestion_byKeywordId =
            "SELECT keyword_id, true_false_question_id " +
            "FROM keywordtruefalsequestion_o " +
            "WHERE keyword_id = #{id}";
    @Select(select_keyword_TFQuestion_byKeywordId)
    List<KeywordTrueFalseQuestion> selectKeywordTFQuestionByKeywordId(int id);

    String select_keyword_TFQuestion_byTFQuestionId =
            "SELECT keyword_id, true_false_question_id " +
            "FROM keywordtruefalsequestion_o " +
            "WHERE true_false_question_id = #{id}";
    @Select(select_keyword_TFQuestion_byTFQuestionId)
    List<KeywordTrueFalseQuestion> selectKeywordTFQuestionByTFQuestionId(int id);


}
