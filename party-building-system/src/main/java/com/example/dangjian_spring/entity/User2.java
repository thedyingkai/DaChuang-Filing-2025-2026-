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
@TableName("User2")
public class User2 {
    @TableId(type = IdType.AUTO)
    private Integer user_id;
    private String user_name;
    private String password;
    private Integer points;
    private Integer role_id; //权限角色id

    @TableField(exist = false)
    private String role_name;
    @TableField(exist = false)
    private String role_permissions;
    @TableField(exist = false)
    private String token;
}
