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
@TableName("draft_view")
public class Draft {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer srid;
    private Integer uid;
    private Integer aid;
    private Integer coid;
    private String content;
    private String title;
    private String save_time;
    private String send_time;
    private String source;
    private Integer status;
    @TableField(exist = false)
    private String uname;
    @TableField(exist = false)
    private String column;
    @TableField(exist = false)
    private Integer bid;
}
