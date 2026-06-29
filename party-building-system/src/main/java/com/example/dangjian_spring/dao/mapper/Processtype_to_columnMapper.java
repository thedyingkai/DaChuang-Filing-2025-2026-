package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Processtype_to_column;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface Processtype_to_columnMapper extends BaseMapper<Processtype_to_column> {
    @Insert("INSERT INTO processtype_to_column_view(ptid,coid,bid) values(#{ptid},#{coid},#{bid}) ")
    int add(Integer ptid, Integer coid,Integer bid);

    @Delete("DELETE FROM processtype_to_column_view where ptid=#{ptid}")
    int deleteByptid(Integer ptid);

    @Delete("DELETE FROM processtype_to_column_view where coid=#{coid} and bid=#{bid}")
    int deleteBycoid(Integer coid, Integer bid);

    /**
     * 栏目 id → 绑定的流程类型 id（用于前端判断该栏目是否可走审核）。
     */
    @Select("SELECT MIN(ptid) AS ptid, coid FROM processtype_to_column_view GROUP BY coid")
    List<Processtype_to_column> selectPtidGroupedByCoid();
}
