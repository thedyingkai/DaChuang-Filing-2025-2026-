package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Column2;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface Column2Mapper extends BaseMapper<Column2>{
    String select_all_columns_sql =
            "SELECT column_id, column_description " +
            "FROM column2_o";
    @Select(select_all_columns_sql)
    List<Column2> allColumns();

    String select_column_byId_sql =
            "SELECT column_id, column_description " +
            "FROM column2_o " +
            "WHERE column_id = #{id}";
    @Select(select_column_byId_sql)
    Column2 selectColumnById(int id);

    String select_column_byDes_sql =
            "SELECT column_id, column_description " +
            "FROM column2_o " +
            "WHERE column_description = #{description}";
    @Select(select_column_byDes_sql)
    Column2 selectColumnByDes(String description);

    String search_columns_byDes_sql =
            "SELECT column_id, column_description " +
            "FROM column2_o " +
            "WHERE column_description LIKE #{keyword}";
    @Select(search_columns_byDes_sql)
    List<Column2> searchColumnsByDes(String keyword);

    String delete_column_sql =
            "DELETE FROM column2_o WHERE column2_o.column_id = #{id}";
    @Delete(delete_column_sql)
    int deleteColumn(int id);

    String update_column_sql =
            "UPDATE column2_o " +
            "SET column_id = #{column_id}, column_description = #{column_description} " +
            "WHERE column_id = #{column_id}";
    @Update(update_column_sql)
    int updateColumn(Column2 column);

    String insert_column_sql =
            "INSERT INTO column2_o(column_id, column_description) " +
            "VALUES (#{column_id}, #{column_description})";
    @Insert(insert_column_sql)
    int addColumn(Column2 column);
}
