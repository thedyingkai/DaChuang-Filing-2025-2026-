package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Draft;
import com.example.dangjian_spring.entity.Submit;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SubmitMapper extends BaseMapper<Submit> {

    @Insert("INSERT INTO submit_view(content,did,source,status,time,title) " +
            "VALUES (#{content},#{did},#{source},0,#{time},#{title})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addSubmit(Submit submit);

    @Update("UPDATE submit_view set status=#{status} where id=#{id}")
    int updateSubmitStatus(Submit submit);

    @Update("UPDATE submit_view set title=#{title},content=#{content},source=#{source} where id=#{id}")
    int updateContent(Submit submit);

}
