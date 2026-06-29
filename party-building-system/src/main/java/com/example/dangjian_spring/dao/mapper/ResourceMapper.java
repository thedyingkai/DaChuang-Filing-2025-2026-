package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Resource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    /** 写入基表 resource（视图 resource_view 仅用于查询；基表列名为 re_*） */
    @Insert("INSERT INTO resource(uid, acid, re_name, re_content, re_savetime, re_type, re_description) " +
            "VALUES (#{uid}, #{acid}, #{name}, #{content}, #{savetime}, #{type}, #{description})")
    @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "reid")
    int insert(Resource resource);

    @Select("SELECT COUNT(1) FROM activity WHERE acid = #{acid}")
    int countActivityByAcid(@Param("acid") Integer acid);

    @Select("SELECT COUNT(1) FROM user1 WHERE uid = #{uid}")
    int countUserByUid(@Param("uid") Integer uid);

    @Select("SELECT * FROM resource_view WHERE uid = #{uid} AND acid = #{acid}")
    List<Resource> selectByUidAndAcid(Integer uid, Integer acid);

    @Select("SELECT * FROM resource_view WHERE acid = #{acid}")
    List<Resource> selectByAcid(Integer acid);

    @Update("<script>Update resource_view SET acid = #{acid} WHERE id IN " +
            "<foreach collection='selectedSourceIdList' item = 'sourceId' open='(' separator=',' close=')'>" +
            "#{sourceId}" +
            "</foreach></script>")
    void batchUpdateAcid(List<Integer> selectedSourceIdList, Integer acid);

    @Select("SELECT resource_view.content, description " +
            "FROM resource_view LEFT JOIN activity_view ON resource_view.acid = activity_view.id " +
            "WHERE resource_view.type = 1 ORDER BY activity_view.time LIMIT 10")
    List<Resource> selectLatestImages();

    @Select("SELECT type, COUNT(*) AS resource_count FROM resource_view GROUP BY type")
    List<Map<String, Object>> countResourceTypes();

    // DATE_FORMAT 中的 % 须写成 %%，否则 MyBatis 会吞掉 %Y/%m 导致 SQL 语法错误
    @Select("SELECT DATE_FORMAT(av.`time`, '%%Y-%%m') AS `ym`, COUNT(rv.id) AS resource_count " +
            "FROM resource_view rv " +
            "RIGHT JOIN activity_view av ON rv.acid = av.id " +
            "WHERE av.`time` IS NOT NULL " +
            "GROUP BY DATE_FORMAT(av.`time`, '%%Y-%%m') " +
            "ORDER BY DATE_FORMAT(av.`time`, '%%Y-%%m')")
    List<Map<String, Object>> countResourceEachMonth();

    @Select("SELECT * FROM resource_view ORDER BY savetime DESC")
    List<Resource> selectAll();

    @Select({
            "<script>",
            "SELECT DISTINCT r.* FROM resource_view r",
            "<if test='joiner != null and joiner != \"\"'>",
            "INNER JOIN participate_view p ON r.acid = p.acid",
            "INNER JOIN user_view u ON p.uid = u.id",
            "WHERE u.username LIKE CONCAT('%', #{joiner}, '%')",
            "   OR u.uname LIKE CONCAT('%', #{joiner}, '%')",
            "</if> ORDER BY savetime DESC",
            "</script>"
    })
    List<Resource> selectByJoiner(String joiner);
}
