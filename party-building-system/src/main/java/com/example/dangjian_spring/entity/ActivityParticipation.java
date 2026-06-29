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
@TableName("participate_view")
public class ActivityParticipation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer acid;
    private Integer uid;
    private Integer type;//0：参与，1：病假；2：事假；3：其他
    private String detail;
}
