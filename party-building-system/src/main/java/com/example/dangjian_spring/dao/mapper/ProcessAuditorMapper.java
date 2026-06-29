package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.ProcessAuditor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcessAuditorMapper extends BaseMapper<ProcessAuditor> {

    @Insert("INSERT into process_auditor_view(pid,uid) values(#{pid},#{uid})")
    int add(Integer pid, Integer uid);

    @Delete("delete from process_auditor_view where pid=#{id}")
    int delete(Integer id);


}
