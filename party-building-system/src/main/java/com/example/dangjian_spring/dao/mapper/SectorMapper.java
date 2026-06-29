package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Sector;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SectorMapper extends BaseMapper<Sector> {

    @Select("select * from sector_view order by id asc")
    List<Sector> selectall();

    @Insert("insert into sector_view(name) values(#{name}) ")
    void add(Sector sector);

    @Delete("DELETE FROM sector_view where id=#{id}")
    void delete(Integer id);

    @Update("update sector_view set name=#{name} where id=#{id}")
    void rename(Sector sector);
}
