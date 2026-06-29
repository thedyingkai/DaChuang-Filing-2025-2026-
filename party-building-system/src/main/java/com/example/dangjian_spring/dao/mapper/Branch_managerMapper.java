package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Branch_manager;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface Branch_managerMapper extends BaseMapper<Branch_manager> {


    @Select("SELECT branch_manager_view.*,user_view.uname,user_view.avatar from branch_manager_view,user_view "+
            "where branch_manager_view.uid=user_view.id AND branch_manager_view.bid=#{bid}")
     List<Branch_manager> selectByBid(Integer bid);

    @Insert("insert into branch_manager_view(bid,name,uid) values(#{bid},#{name},#{uid}) ")
    void add(Branch_manager branch_manager);

    @Update("UPDATE branch_manager_view set name = #{name} where id = #{id}")
    void rename(Branch_manager branch_manager);

    @Delete("DELETE from branch_manager_view where id=#{id}")
    void delete(Integer id);

    @Update("UPDATE branch_manager_view set uid = #{uid} WHERE id = #{id}")
    void changeManager(int id, int uid);
}
