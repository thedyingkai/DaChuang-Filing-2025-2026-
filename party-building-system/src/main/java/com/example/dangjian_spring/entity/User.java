package com.example.dangjian_spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@TableName("user_view")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer cid;//用户类型
    private Integer gid;//所属党组织
    private Integer bid;//所属支部
    private Integer deid;//所属科室
    private String uname;
    private String psw;
    private Integer points;
    private String username;
    private String avatar;

    @TableField(exist = false)
    private String token;
    @TableField(exist = false)
    private String permissions;//主页管理/创建专栏/文章审核/文章发布/管理活动/答题/管理人员
    @TableField(exist = false)
    private String cname;
    @TableField(exist = false)
    private String branchName;
    @TableField(exist = false)
    private String groupName;
    @TableField(exist = false)
    private String departName;
    @TableField(exist = false)
    private String sectorName;
}
