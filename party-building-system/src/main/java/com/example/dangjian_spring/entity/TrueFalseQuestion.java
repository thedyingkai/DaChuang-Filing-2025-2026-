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
@TableName("truefalsequestion_o")
public class TrueFalseQuestion {
    @TableId(type = IdType.AUTO)
    private Integer true_false_question_id;
    private String question_description;
    private String correct_answer;
    private String question_analysis;

    @TableField(exist = false)
    private String question_order_in_paper;
}
