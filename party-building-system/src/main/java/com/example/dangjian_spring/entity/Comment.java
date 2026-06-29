package com.example.dangjian_spring.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("comments_view")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private Integer audit_uid;
    private Integer aid;
    private Integer parent_id;
    private String content;
    private Integer status;
    private String send_time;
    @TableField(exist = false)
    private Integer like;
    @TableField(exist = false)
    private String title;
    @TableField(exist = false)
    private String uname;
    @TableField(exist = false)
    private String avatar;
    @TableField(exist = false)
    private List<Comment> children;
}
