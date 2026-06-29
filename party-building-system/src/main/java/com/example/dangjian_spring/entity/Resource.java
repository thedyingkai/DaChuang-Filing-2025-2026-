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
@TableName("resource_view")
public class Resource {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer acid;
    private String name;
    private String content;
    private String savetime;
    private Integer type;
    private Integer uid;
    private String description;
    @TableField(exist = false)
    private String uname;
}
