package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.KeywordFillInTheBlankQuestion;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KeywordFIQuestionMapper extends BaseMapper<KeywordFillInTheBlankQuestion> {
    String add_keyword_in_FIQuestion_sql =
            "INSERT INTO keywordfillintheblankquestion_o(keyword_id, fill_in_the_blank_question_id) " +
            "VALUES (#{keyword_id}, #{fill_in_the_blank_question_id})";
    @Insert(add_keyword_in_FIQuestion_sql)
    int addKeywordInFIQuestion(KeywordFillInTheBlankQuestion keywordFillInTheBlankQuestion);

    String delete_keyword_in_FIQuestion_sql =
            "DELETE FROM keywordfillintheblankquestion_o WHERE keyword_id = #{keyword_id} AND fill_in_the_blank_question_id = #{fill_in_the_blank_question_id}";
    @Delete(delete_keyword_in_FIQuestion_sql)
    int deleteKeywordFIQuestion(KeywordFillInTheBlankQuestion keywordFillInTheBlankQuestion);

    String select_keyword_FIQuestion_byKeywordId =
            "SELECT keyword_id, fill_in_the_blank_question_id " +
            "FROM keywordfillintheblankquestion_o " +
            "WHERE keyword_id = #{id}";
    @Select(select_keyword_FIQuestion_byKeywordId)
    List<KeywordFillInTheBlankQuestion> selectKeywordFIQuestionByKeywordId(int id);

    String select_keyword_FIQuestion_byTFQuestionId =
            "SELECT keyword_id, fill_in_the_blank_question_id " +
            "FROM keywordfillintheblankquestion_o " +
            "WHERE fill_in_the_blank_question_id = #{id}";
    @Select(select_keyword_FIQuestion_byTFQuestionId)
    List<KeywordFillInTheBlankQuestion> selectKeywordFIQuestionByFIQuestionId(int id);


}
