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
@TableName("branch_manager_view")
public class Branch_manager {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer bid;
    private Integer uid;
    @TableField(exist = false)
    private String uname;
    @TableField(exist = false)
    private String avatar;
}
