package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.KeywordMultipleChoiceQuestion;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KeywordMCQuestionMapper extends BaseMapper<KeywordMultipleChoiceQuestion> {
    String add_keyword_in_MCQuestion_sql =
            "INSERT INTO keywordmultiplechoicequestion_o(keyword_id, multiple_choice_question_id) " +
            "VALUES (#{keyword_id}, #{multiple_choice_question_id})";
    @Insert(add_keyword_in_MCQuestion_sql)
    int addKeywordInMCQuestion(KeywordMultipleChoiceQuestion keywordMultipleChoiceQuestion);

    String delete_keyword_in_MCQuestion_sql =
            "DELETE FROM keywordmultiplechoicequestion_o WHERE keyword_id = #{keyword_id} AND multiple_choice_question_id = #{multiple_choice_question_id}";
    @Delete(delete_keyword_in_MCQuestion_sql)
    int deleteKeywordMCQuestion(KeywordMultipleChoiceQuestion keywordMultipleChoiceQuestion);

    String delete_keyword_by_MCQuestion_sql =
            "DELETE FROM keywordmultiplechoicequestion_o WHERE multiple_choice_question_id = #{id}";
    @Delete(delete_keyword_by_MCQuestion_sql)
    int deleteKeywordByMCQuestionId(int id);

    String select_keyword_MCQuestion_byKeywordId =
            "SELECT keyword_id, multiple_choice_question_id " +
            "FROM keywordmultiplechoicequestion_o " +
            "WHERE keyword_id = #{id}";
    @Select(select_keyword_MCQuestion_byKeywordId)
    List<KeywordMultipleChoiceQuestion> selectKeywordMCQuestionByKeywordId(int id);

    String select_keyword_MCQuestion_byTFQuestionId =
            "SELECT keyword_id, multiple_choice_question_id " +
            "FROM keywordmultiplechoicequestion_o " +
            "WHERE multiple_choice_question_id = #{id}";
    @Select(select_keyword_MCQuestion_byTFQuestionId)
    List<KeywordMultipleChoiceQuestion> selectKeywordMCQuestionByTFQuestionId(int id);


}
