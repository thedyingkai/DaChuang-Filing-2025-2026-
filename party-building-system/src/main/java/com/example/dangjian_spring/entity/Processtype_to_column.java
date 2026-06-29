package com.example.dangjian_spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TableName("processtype_to_column_view")
public class Processtype_to_column {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer coid;
    private Integer ptid;
    private Integer gid;
}
