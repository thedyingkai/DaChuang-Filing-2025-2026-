package com.example.dangjian_spring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@TableName("group_view")
public class Group {
    @TableId(type = IdType.AUTO)
    private Integer gid;
    private Integer bid;
    private String name;
    private Integer leader_uid;

}
