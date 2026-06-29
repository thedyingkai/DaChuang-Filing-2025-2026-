package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Chara;
import org.apache.ibatis.annotations.*;
import java.util.List;


@Mapper
public interface CharaMapper extends BaseMapper<Chara> {

    @Select("SELECT * FROM chara_view")
    List<Chara> selectAll();

    @Insert("INSERT INTO chara_view (id, cname, permissions) VALUES (#{id}, #{cname}, #{permissions})")
    int insert(Chara chara_view);

    @Update("UPDATE chara_view SET cname = #{cname}, permissions = #{permissions} WHERE id = #{id}")
    void updateChara(Chara chara_view);

    @Delete("DELETE FROM chara_view WHERE id = #{id}")
    void deleteChara(Integer id);

    @Select("SELECT * FROM chara_view WHERE id = #{id}")
    Chara selectByCid(Integer id);

    @Select("SELECT * FROM chara_view WHERE cname = #{cname}")
    Chara selectByCname(String cname);

    @Select("SELECT id FROM chara_view WHERE cname = #{cname}")
    int getCidByCname(String cname);
}