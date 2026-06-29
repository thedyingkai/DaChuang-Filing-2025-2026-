package com.example.dangjian_spring.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("Participation")
public class Participation {
    /** 参与记录的业务主键之一；与 test_paper_id 组成逻辑唯一（非表自增 id） */
    @TableId(type = IdType.INPUT)
    @JsonProperty("user_id")
    private Integer user_id;
    @JsonProperty("test_paper_id")
    private Integer test_paper_id;
    @JsonProperty("participation_time")
    private String participation_time;
    @JsonProperty("test_record")
    private String test_record;

    @TableField(exist = false)
    private String user_name;

}
