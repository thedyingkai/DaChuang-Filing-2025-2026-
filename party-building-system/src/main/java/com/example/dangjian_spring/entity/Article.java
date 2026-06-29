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
@TableName("article_view")
public class Article {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer coid;
    private Integer uid;
    private String content;
    private String publish_time;
    private String title;
    private String source;
    private Integer show;
    @TableField(exist = false)
    private String column;
    @TableField(exist = false)
    private Integer bid;
    @TableField(exist = false)
    private String name;
}
