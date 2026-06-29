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
@TableName("sector_view")
public class Sector {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;

    @TableField(exist = false)
    private List<Department> children;
}
