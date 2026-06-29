package com.example.dangjian_spring.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.HomePic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HomePicMapper extends BaseMapper<HomePic> {
    String select_all_homePics = "SELECT id, source, `index`, link, text " +
            "FROM home_show_picture_view " +
            "ORDER BY `index` ASC, id ASC ";
    @Select(select_all_homePics)
    List<HomePic> select_all_homePic();


    String delete_homePic_by_id = "DELETE FROM home_show_picture_view WHERE id = #{id}";
    @Delete(delete_homePic_by_id)
    int delete_homePic_by_id(int id);

    String update_homePic_by_id = "UPDATE home_show_picture_view " +
            "SET id = #{id}, source = #{source}, `index` = #{index}, link = #{link}, text = #{text} " +
            "WHERE id = #{id}";
    @Update(update_homePic_by_id)
    int update_homePic_by_id(HomePic homePic);

    String add_homePic = "INSERT INTO home_show_picture_view(id, source, `index`, link, text) " +
            "VALUES (#{id}, #{source}, #{index}, #{link}, #{text})";
    @Insert(add_homePic)
    int add_homePic(HomePic homePic);

    
    String delete_all_homePics = "DELETE FROM home_show_picture_view";
    @Delete(delete_all_homePics)
    int delete_all_homePics();





}
