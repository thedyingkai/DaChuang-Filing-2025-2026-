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
@TableName("audit_view")
public class Audit {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private Integer srid;
    private Integer next;
    private Integer status;
    private String advice;
    private String time;
    @TableField(exist = false)
    private Integer editor_uid;
    @TableField(exist = false)
    private String editor_uname;
    @TableField(exist = false)
    private String content;
    @TableField(exist = false)
    private String title;
    @TableField(exist = false)
    private String source;
    @TableField(exist = false)
    private String uname;
    @TableField(exist = false)
    private String column;
    @TableField(exist = false)
    private Integer did;
    /** 草稿栏目 id（待审列表联表查询用） */
    @TableField(exist = false)
    private Integer coid;
    /** 提交/送审时间（来自 draft_view，供待审列表展示） */
    @TableField(exist = false)
    private String saveTime;
}
