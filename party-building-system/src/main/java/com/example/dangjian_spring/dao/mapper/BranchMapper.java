package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Branch;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BranchMapper extends BaseMapper<Branch> {

    @Select("select * from branch_view order by bid")
    List<Branch> selectAll();

    @Select("select * from branch_view where bid=#{bid}")
    Branch selectByBid( Integer bid);

    @Insert("insert into branch_view(name) values (#{name})")
    int add(Branch branch);

    @Delete("DELETE from branch_view where bid=#{id}")
    void delete(Integer id);

    @Update("UPDATE branch_view set name=#{name} where bid=#{bid}")
    void update(Branch branch);
}
