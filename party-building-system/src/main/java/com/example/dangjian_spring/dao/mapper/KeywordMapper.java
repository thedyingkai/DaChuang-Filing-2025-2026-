package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Keyword;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface KeywordMapper extends BaseMapper<Keyword> {
    String select_all_keywords_sql =
            "SELECT keyword_id, keyword_description, column_id " +
            "FROM keyword_o";

    @Select(select_all_keywords_sql)
    List<Keyword> allKeywords();

    String select_keywords_in_TFQuestion =
            "SELECT keyword_o.keyword_id, keyword_description, column_id " +
            "FROM keyword_o JOIN keywordtruefalsequestion_o k on keyword_o.keyword_id = k.keyword_id " +
            "WHERE k.true_false_question_id = #{id}";
    @Select(select_keywords_in_TFQuestion)
    List<Keyword> selectKeywordsInTFQuestion(int id);

    String select_keywords_in_MCQuestion =
            "SELECT keyword_o.keyword_id, keyword_description, column_id " +
            "FROM keyword_o JOIN keywordmultiplechoicequestion_o k on keyword_o.keyword_id = k.keyword_id " +
            "WHERE k.multiple_choice_question_id = #{id}";
    @Select(select_keywords_in_MCQuestion)
    List<Keyword> selectKeywordsInMCQuestion(int id);

    String select_keywords_in_FIQuestion =
            "SELECT  keyword_o.keyword_id, keyword_description, column_id " +
            "FROM keyword_o JOIN keywordfillintheblankquestion_o k on keyword_o.keyword_id = k.keyword_id " +
            "WHERE k.fill_in_the_blank_question_id = #{id}";
    @Select(select_keywords_in_FIQuestion)
    List<Keyword> selectKeywordsInFIQuestion(int id);

    String select_keyword_byId_sql =
            "SELECT keyword_id, keyword_description, column_id " +
            "FROM keyword_o " +
            "WHERE keyword_id = #{id}";

    @Select(select_keyword_byId_sql)
    Keyword selectKeywordById(int id);

    String select_keyword_byDes_sql =
            "SELECT keyword_id, keyword_description, column_id " +
            "FROM keyword_o " +
            "WHERE keyword_description = #{description}";

    @Select(select_keyword_byDes_sql)
    Keyword selectKeywordByDes(String description);

    String search_keyword_byDes_sql =
            "SELECT keyword_id, keyword_description, column_id " +
            "FROM keyword_o " +
            "WHERE keyword_description LIKE #{keyword}";

    @Select(search_keyword_byDes_sql)
    List<Keyword> searchKeywordByDes(String keyword);

    String select_keywords_same_column =
            "SELECT keyword_id, keyword_description, column_id " +
            "FROM keyword_o " +
            "WHERE column_id = #{id}";
    @Select(select_keywords_same_column)
    List<Keyword> selectKeywordsSameColumn(int id);

    String insert_keyword_sql =
            "INSERT INTO keyword_o(keyword_description, column_id) " +
            "VALUES (#{keyword_description}, #{column_id})";

    @Insert(insert_keyword_sql)
    @Options(useGeneratedKeys = true, keyProperty = "keyword_id")
    int addKeyword(Keyword k);

    String update_keyword_sql =
            "UPDATE keyword_o " +
            "SET keyword_id = #{keyword_id}, keyword_description = #{keyword_description}, column_id = #{column_id} " +
            "WHERE keyword_id = #{keyword_id}";
    @Update(update_keyword_sql)
    int updateKeyword(Keyword k);

    String delete_keyword_sql =
            "DELETE FROM keyword_o WHERE keyword_id = #{id}";
    @Delete(delete_keyword_sql)
    int deleteKeyword(int id);


}
