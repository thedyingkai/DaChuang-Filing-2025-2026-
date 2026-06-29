package com.example.dangjian_spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TableName("processtype_view")
public class ProcessType {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer number;
    private Integer pid;
    private Integer type;
    /** 业务上的组织维度；若库表 processtype_view 无 bid 列，则仅存于关联表或前端传参 */
    @TableField(exist = false)
    private Integer bid;

    @TableField(exist = false)
    private List<Process> processList;
    @TableField(exist = false)
    private Integer coid;
    @TableField(exist = false)
    private String co_name;
    @TableField(exist = false)
    private Integer uid;
}

