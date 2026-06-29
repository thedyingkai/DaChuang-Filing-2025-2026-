package com.example.dangjian_spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@TableName("paperfillintheblankquestion_o")
public class PaperFillInTheBlankQuestion {
    @TableId(type = IdType.AUTO)
    private Integer test_paper_id;
    private Integer fill_in_the_blank_question_id;
    private Integer question_order_in_paper;
}
