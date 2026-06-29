package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.PaperTrueFalseQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface PaperTFQuestionMapper extends BaseMapper<PaperTrueFalseQuestion> {
    String select_all_TFQuestion_in_paper_sql =
            "SELECT test_paper_id, true_false_question_id, question_order_in_paper " +
            "FROM papertruefalsequestion_o " +
            "WHERE test_paper_id = #{id} " +
            "ORDER BY question_order_in_paper";
    @Select(select_all_TFQuestion_in_paper_sql)
    List<PaperTrueFalseQuestion> allTFQuestionsInPaper(int id);

    String add_TFQuestion_to_paper_sql =
            "INSERT INTO papertruefalsequestion_o(test_paper_id, true_false_question_id, question_order_in_paper) " +
            "VALUES (#{test_paper_id}, #{true_false_question_id}, #{question_order_in_paper})";
    @Insert(add_TFQuestion_to_paper_sql)
    int addTFQuestionInPaper(PaperTrueFalseQuestion paperTrueFalseQuestion);

    String delete_TFQuestion_in_paper_sql =
            "DELETE FROM papertruefalsequestion_o WHERE test_paper_id = #{test_paper_id} AND true_false_question_id = #{true_false_question_id}";
    @Delete(delete_TFQuestion_in_paper_sql)
    int deleteTFQuestionInPaper(PaperTrueFalseQuestion paperTrueFalseQuestion);

    String update_TFQuestion_in_paper_sql =
            "UPDATE papertruefalsequestion_o " +
            "SET test_paper_id = #{test_paper_id}, true_false_question_id = #{true_false_question_id}, question_order_in_paper = #{question_order_in_paper} " +
            "WHERE test_paper_id = #{test_paper_id} AND true_false_question_id = #{true_false_question_id}";
    @Update(update_TFQuestion_in_paper_sql)
    int updateTFQuestionInPaper(PaperTrueFalseQuestion paperTrueFalseQuestion);

    @Select("SELECT COUNT(*) FROM papertruefalsequestion_o WHERE test_paper_id = #{paperId} AND true_false_question_id = #{questionId}")
    int countTfInPaper(@Param("paperId") int paperId, @Param("questionId") int questionId);

    /** 删除题目前：移除该题在所有试卷中的引用（允许多卷共用同一题库题） */
    @Delete("DELETE FROM papertruefalsequestion_o WHERE true_false_question_id = #{questionId}")
    int deletePaperLinksByTfQuestionId(int questionId);
}
