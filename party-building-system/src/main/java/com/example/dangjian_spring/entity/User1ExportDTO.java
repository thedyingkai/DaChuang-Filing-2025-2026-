package com.example.dangjian_spring.entity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User1ExportDTO {
    private Integer uid;
    private String uname;
    private String psw;
    private String cname;
}