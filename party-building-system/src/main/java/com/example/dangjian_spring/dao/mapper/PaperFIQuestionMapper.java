package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.PaperFillInTheBlankQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface PaperFIQuestionMapper extends BaseMapper<PaperFillInTheBlankQuestion> {
    String select_all_FIQuestion_in_paper_sql =
            "SELECT test_paper_id, fill_in_the_blank_question_id, question_order_in_paper " +
            "FROM paperfillintheblankquestion_o " +
            "WHERE test_paper_id = #{id} " +
            "ORDER BY question_order_in_paper";
    @Select(select_all_FIQuestion_in_paper_sql)
    List<PaperFillInTheBlankQuestion> allFIQuestionsInPaper(int id);

    String add_FIQuestion_to_paper_sql =
            "INSERT INTO paperfillintheblankquestion_o(test_paper_id, fill_in_the_blank_question_id, question_order_in_paper) " +
            "VALUES (#{test_paper_id}, #{fill_in_the_blank_question_id}, #{question_order_in_paper})";
    @Insert(add_FIQuestion_to_paper_sql)
    int addFIQuestionInPaper(PaperFillInTheBlankQuestion paperFillInTheBlankQuestion);

    String delete_FIQuestion_in_paper_sql =
            "DELETE FROM paperfillintheblankquestion_o WHERE test_paper_id = #{test_paper_id} AND fill_in_the_blank_question_id = #{fill_in_the_blank_question_id}";
    @Delete(delete_FIQuestion_in_paper_sql)
    int deleteFIQuestionInPaper(PaperFillInTheBlankQuestion paperFillInTheBlankQuestion);

    String update_FIQuestion_in_paper_sql =
            "UPDATE paperfillintheblankquestion_o " +
            "SET test_paper_id = #{test_paper_id}, fill_in_the_blank_question_id = #{fill_in_the_blank_question_id}, question_order_in_paper = #{question_order_in_paper} " +
            "WHERE test_paper_id = #{test_paper_id} AND fill_in_the_blank_question_id = #{fill_in_the_blank_question_id}";
    @Update(update_FIQuestion_in_paper_sql)
    int updateFIQuestionInPaper(PaperFillInTheBlankQuestion paperFillInTheBlankQuestion);

    @Select("SELECT COUNT(*) FROM paperfillintheblankquestion_o WHERE test_paper_id = #{paperId} AND fill_in_the_blank_question_id = #{questionId}")
    int countFiInPaper(@Param("paperId") int paperId, @Param("questionId") int questionId);

    @Delete("DELETE FROM paperfillintheblankquestion_o WHERE fill_in_the_blank_question_id = #{questionId}")
    int deletePaperLinksByFiQuestionId(int questionId);
}
