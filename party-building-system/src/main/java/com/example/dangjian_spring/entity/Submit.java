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
@TableName("submit_view")
public class Submit {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer did;
    private Integer status;
    private String title;
    private String content;
    private String source;
    private String time;
    @TableField(exist = false)
    private Integer uid;
    @TableField(exist = false)
    private String uname;
}
