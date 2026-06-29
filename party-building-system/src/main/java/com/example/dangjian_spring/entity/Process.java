package com.example.dangjian_spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TableName("process_view")
public class Process {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer last;
    private Integer next;
    @TableField(exist = false)
    private Integer uid;
    @TableField(exist = false)
    private String uname;
    @TableField(exist = false)
    private Integer before;
}
