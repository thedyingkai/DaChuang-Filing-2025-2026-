package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.MultipleChoiceQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MCQuestionMapper extends BaseMapper<MultipleChoiceQuestion>{
    String select_all_MCQuestions =
            "SELECT multiple_choice_question_id, question_description, correct_answer, question_analysis " +
            "FROM multiplechoicequestion_o " +
            "ORDER BY multiple_choice_question_id ASC";
    @Select(select_all_MCQuestions)
    List<MultipleChoiceQuestion> selectAllMCQuestions();

    String select_all_MCQuestions_in_paper_sql =
            "SELECT multiplechoicequestion_o.multiple_choice_question_id, question_description, correct_answer, question_analysis, papermultiplechoicequestion_o.question_order_in_paper " +
            "FROM multiplechoicequestion_o JOIN papermultiplechoicequestion_o on multiplechoicequestion_o.multiple_choice_question_id = papermultiplechoicequestion_o.multiple_choice_question_id " +
            "WHERE papermultiplechoicequestion_o.test_paper_id = #{id} ";
    @Select(select_all_MCQuestions_in_paper_sql)
    List<MultipleChoiceQuestion> selectAllMCQuestionsInPaper(int id);

    String select_MCQuestion_byId_sql =
            "SELECT multiple_choice_question_id, question_description, correct_answer, question_analysis " +
            "FROM multiplechoicequestion_o " +
            "WHERE multiple_choice_question_id = #{id}";
    @Select(select_MCQuestion_byId_sql)
    MultipleChoiceQuestion selectMCQuestionById(int id);

    String select_MCQuestion_byDes =
            "SELECT multiple_choice_question_id, question_description, correct_answer, question_analysis " +
            "FROM multiplechoicequestion_o " +
            "WHERE question_description = #{description}";
    @Select(select_MCQuestion_byDes)
    MultipleChoiceQuestion selectMCQuestionByDes(String description);


    String update_MCQuestion_sql =
            "UPDATE multiplechoicequestion_o " +
            "SET multiple_choice_question_id = #{multiple_choice_question_id}, question_description = #{question_description}, " +
            "correct_answer = #{correct_answer}, question_analysis = #{question_analysis} " +
            "WHERE multiple_choice_question_id = #{multiple_choice_question_id}";
    @Update(update_MCQuestion_sql)
    int updateMCQuestion(MultipleChoiceQuestion question);

    String add_MCQuestion_sql =
            "INSERT INTO multiplechoicequestion_o(question_description, correct_answer, question_analysis) " +
            "VALUES (#{question_description}, #{correct_answer}, #{question_analysis})";
    @Insert(add_MCQuestion_sql)
    @Options(useGeneratedKeys = true, keyProperty = "multiple_choice_question_id", keyColumn = "multiple_choice_question_id")
    int addMCQuestion(MultipleChoiceQuestion question);

    String delete_MCQuestion_byId_sql =
            "DELETE FROM multiplechoicequestion_o WHERE multiple_choice_question_id = #{id}";
    @Delete(delete_MCQuestion_byId_sql)
    int deleteMCQuestionById(int id);

    String search_MCQuestion_description_byKeyword =
            "SELECT multiple_choice_question_id, question_description, correct_answer, question_analysis " +
            "FROM multiplechoicequestion_o " +
            "WHERE question_description LIKE #{keyword}";
    @Select(search_MCQuestion_description_byKeyword)
    List<MultipleChoiceQuestion> searchMCQuestionsDescription(String keyword);

    String search_MCQuestion_analysis_byKeyword =
            "SELECT multiple_choice_question_id, question_description, correct_answer, question_analysis " +
            "FROM multiplechoicequestion_o " +
            "WHERE question_analysis LIKE #{keyword}";
    @Select(search_MCQuestion_analysis_byKeyword)
    List<MultipleChoiceQuestion> searchMCQuestionsAnalysis(String keyword);

    String search_MCQuestion_byKeyword_sql =
            "SELECT keywordmultiplechoicequestion_o.multiple_choice_question_id, question_description, correct_answer, question_analysis " +
            "FROM multiplechoicequestion_o " +
            "JOIN keywordmultiplechoicequestion_o ON multiplechoicequestion_o.multiple_choice_question_id = keywordmultiplechoicequestion_o.multiple_choice_question_id " +
            "JOIN keyword_o ON keywordmultiplechoicequestion_o.keyword_id = keyword_o.keyword_id " +
            "WHERE keyword_o.keyword_description LIKE #{keyword}";
    @Select(search_MCQuestion_byKeyword_sql)
    List<MultipleChoiceQuestion> searchMCQuestionsKeyword(String keyword);


}
