package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.MultipleChoiceOption;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OptionMapper extends BaseMapper<MultipleChoiceOption>{
    String select_all_options_in_question_sql =
            "SELECT option_id, order_in_question, option_description, is_correct, multiplechoiceoption_o.multiple_choice_question_id " +
            "FROM multiplechoiceoption_o JOIN multiplechoicequestion_o " +
            "ON multiplechoiceoption_o.multiple_choice_question_id = multiplechoicequestion_o.multiple_choice_question_id " +
            "WHERE multiplechoiceoption_o.multiple_choice_question_id = #{id} " +
            "ORDER BY order_in_question";
    @Select(select_all_options_in_question_sql)
    List<MultipleChoiceOption> allOptionsInQuestion(int id);

    String select_all_options_sql =
            "SELECT option_id, order_in_question, option_description, is_correct, multiple_choice_question_id " +
            "FROM multiplechoiceoption_o ";
    @Select(select_all_options_sql)
    List<MultipleChoiceOption> allOptions();

    String select_option_byId_sql =
            "SELECT option_id, order_in_question, option_description, is_correct, multiplechoiceoption_o.multiple_choice_question_id " +
            "FROM multiplechoiceoption_o JOIN multiplechoicequestion_o " +
            "ON multiplechoiceoption_o.multiple_choice_question_id = multiplechoicequestion_o.multiple_choice_question_id " +
            "WHERE option_id = #{id}";
    @Select(select_option_byId_sql)
    MultipleChoiceOption selectOptionById(int id);

    String update_option_sql =
            "UPDATE multiplechoiceoption_o " +
            "SET option_id = #{option_id}, order_in_question = #{order_in_question}, " +
            "option_description = #{option_description}, is_correct = #{is_correct}, " +
            "multiple_choice_question_id = #{multiple_choice_question_id} " +
            "WHERE option_id = #{option_id}";
    @Update(update_option_sql)
    int updateOption(MultipleChoiceOption option);

    String insert_option_sql =
            "INSERT INTO multiplechoiceoption_o(order_in_question, option_description, is_correct, multiple_choice_question_id) " +
            "VALUES (#{order_in_question}, #{option_description}, #{is_correct}, #{multiple_choice_question_id})";
    @Insert(insert_option_sql)
    @Options(useGeneratedKeys = true, keyProperty = "option_id", keyColumn = "option_id")
    int addOption(MultipleChoiceOption option);

    String delete_option_sql =
            "DELETE FROM multiplechoiceoption_o WHERE option_id = #{id}";
    @Delete(delete_option_sql)
    int deleteOption(int id);
}
