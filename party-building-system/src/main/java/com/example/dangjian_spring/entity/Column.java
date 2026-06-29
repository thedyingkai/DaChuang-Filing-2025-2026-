package com.example.dangjian_spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@TableName("column_view")
public class Column {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer parent_id;
    private String name;
    private Integer index;
    private Integer type;
    @TableField(exist = false)
    private List<Column> child;
    @TableField(exist = false)
    private String is_empty;
    @TableField(exist = false)
    private Integer ptid;
}
