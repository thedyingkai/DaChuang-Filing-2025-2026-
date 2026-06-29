package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Department;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("select * from department_view")
    List<Department> selectAll();

    @Select("select *,name as label,id as value from department_view where seid=#{seid}")
    List<Department> selectDepartmentByseid(Integer seid);

    @Insert("insert into department_view(name,seid) values (#{name},#{seid})")
    void add(Department department);

    @Delete("DELETE FROM department_view where id=#{id}")
    void delete(Integer id);

    @Update("UPDATE department_view set name=#{name} where id=#{id}")
    void rename(Department department);

}
