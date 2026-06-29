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
@TableName("testpaper_o")
public class TestPaper {
    @TableId(type = IdType.AUTO)
    private Integer test_paper_id;
    private String paper_description;
    private String grouping_method;
    private Integer points_reward;
    private String create_date;
    private String deadline;
}
