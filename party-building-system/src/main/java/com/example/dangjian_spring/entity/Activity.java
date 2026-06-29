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
@TableName("activity_view")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private String name;
    private Integer type;
    private String time;
    private String content;
    private String cover_image;
    @TableField(exist = false)
    private String is_empty;
    @TableField(exist = false)
    private String uname;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private List<ActivityParticipation> members;
    @TableField(exist = false)
    private Integer participateCount;
    @TableField(exist = false)
    private Integer sickLeaveCount;
    @TableField(exist = false)
    private Integer personalLeaveCount;
    @TableField(exist = false)
    private Integer otherCount;
}
