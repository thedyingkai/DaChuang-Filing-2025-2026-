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
@TableName("depatment_view")
public class Department {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer seid;
    @TableField(exist = false)
    private String label;
    @TableField(exist = false)
    private Integer value;
}
