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
@TableName("keywordfillintheblankquestion_o")
public class KeywordFillInTheBlankQuestion {
    @TableId(type = IdType.AUTO)
    private Integer keyword_id;
    private Integer fill_in_the_blank_question_id;

}
