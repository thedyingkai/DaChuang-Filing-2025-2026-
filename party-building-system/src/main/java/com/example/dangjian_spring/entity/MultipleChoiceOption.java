package com.example.dangjian_spring.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("multiplechoiceoption_o")
public class MultipleChoiceOption {
    @TableId(type = IdType.AUTO)
    private Integer option_id;
    private Integer order_in_question;
    private String option_description;
    private boolean is_correct;
    private Integer multiple_choice_question_id;
}
