package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.ActivityParticipation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ActivityParticipationMapper {

    @Insert("INSERT INTO participate_view (acid, uid, type, detail) VALUES(#{acid}, #{uid}, #{type}, #{detail})")
    void insert(ActivityParticipation user);

}
