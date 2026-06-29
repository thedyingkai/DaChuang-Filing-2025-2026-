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
@TableName("EditMultipleChoiceQuestion")
public class EditMultipleChoiceQuestion {
    @TableId(type = IdType.AUTO)
    private Integer user_id;
    private Integer multiple_choice_question_id;

}
