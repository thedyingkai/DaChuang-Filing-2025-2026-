package com.example.dangjian_spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@TableName("papertruefalsequestion_o")
public class PaperTrueFalseQuestion {
    @TableId(type = IdType.AUTO)
    private Integer test_paper_id;
    private Integer true_false_question_id;
    private Integer question_order_in_paper;

}
