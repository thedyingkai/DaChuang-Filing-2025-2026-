package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.PaperMultipleChoiceQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface PaperMCQuestionMapper extends BaseMapper<PaperMultipleChoiceQuestion> {
    String select_all_MCQuestion_in_paper_sql =
            "SELECT test_paper_id, multiple_choice_question_id, question_order_in_paper " +
            "FROM papermultiplechoicequestion_o " +
            "WHERE test_paper_id = #{id} " +
            "ORDER BY question_order_in_paper";
    @Select(select_all_MCQuestion_in_paper_sql)
    List<PaperMultipleChoiceQuestion> allMCQuestionsInPaper(int id);

    String add_MCQuestion_to_paper_sql =
            "INSERT INTO papermultiplechoicequestion_o(test_paper_id, multiple_choice_question_id, question_order_in_paper) " +
            "VALUES (#{test_paper_id}, #{multiple_choice_question_id}, #{question_order_in_paper})";
    @Insert(add_MCQuestion_to_paper_sql)
    int addMCQuestionInPaper(PaperMultipleChoiceQuestion paperMultipleChoiceQuestion);

    String delete_MCQuestion_in_paper_sql =
            "DELETE FROM papermultiplechoicequestion_o WHERE test_paper_id = #{test_paper_id} AND multiple_choice_question_id = #{multiple_choice_question_id}";
    @Delete(delete_MCQuestion_in_paper_sql)
    int deleteMCQuestionInPaper(PaperMultipleChoiceQuestion paperMultipleChoiceQuestion);

    String update_MCQuestion_in_paper_sql =
            "UPDATE papermultiplechoicequestion_o " +
            "SET test_paper_id = #{test_paper_id}, multiple_choice_question_id = #{multiple_choice_question_id}, question_order_in_paper = #{question_order_in_paper} " +
            "WHERE test_paper_id = #{test_paper_id} AND multiple_choice_question_id = #{multiple_choice_question_id}";
    @Update(update_MCQuestion_in_paper_sql)
    int updateMCQuestionInPaper(PaperMultipleChoiceQuestion paperMultipleChoiceQuestion);

    @Select("SELECT COUNT(*) FROM papermultiplechoicequestion_o WHERE test_paper_id = #{paperId} AND multiple_choice_question_id = #{questionId}")
    int countMcInPaper(@Param("paperId") int paperId, @Param("questionId") int questionId);

    @Delete("DELETE FROM papermultiplechoicequestion_o WHERE multiple_choice_question_id = #{questionId}")
    int deletePaperLinksByMcQuestionId(int questionId);
}
