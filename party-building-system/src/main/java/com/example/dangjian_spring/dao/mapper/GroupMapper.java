package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Branch;
import com.example.dangjian_spring.entity.Group;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {

    @Select("SELECT * FROM group_view where bid=#{bid} order by gid")
    List<Group> selectByBid(Integer bid);

    @Insert("INSERT into group_view(bid,name) values (#{bid},#{name})")
    void add(Group group);

    @Update("UPDATE group_view set name=#{name} where gid=#{gid}")
    void update(Group group);

    @Update("UPDATE group_view set leader_uid=#{leader_uid} where gid=#{gid}")
    void setleader(Group group);

    @Delete("DELETE from group_view where gid=#{gid}")
    void delete(Integer gid);
}
