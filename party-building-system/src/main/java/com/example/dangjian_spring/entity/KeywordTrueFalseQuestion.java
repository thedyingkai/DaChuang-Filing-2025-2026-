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
@TableName("keywordtruefalsequestion_o")
public class KeywordTrueFalseQuestion {
    @TableId(type = IdType.AUTO)
    private Integer keyword_id;
    private Integer true_false_question_id;

}
