package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Column;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface ColumnMapper extends BaseMapper<Column> {

    /**
     * 部分库中 name 为 BLOB/TEXT，select * 时驱动按 BLOB 映射会导致 JSON 异常、前端无法展示栏目名。
     * 显式列并 CAST(name) 为字符串。
     */
    @Select("SELECT column_view.id, column_view.parent_id, CAST(column_view.name AS CHAR(1024)) AS name, " +
            "column_view.`index`, column_view.`type`, " +
            "(NOT EXISTS(SELECT * FROM article_view WHERE coid = column_view.id)) AS is_empty, " +
            "CAST(NULL AS SIGNED) AS ptid " +
            "FROM column_view " +
            "ORDER BY column_view.`index`")
    List<Column> selectAll();

    @Select("SELECT column_view.id, column_view.parent_id, CAST(column_view.name AS CHAR(1024)) AS name, " +
            "column_view.`index`, column_view.`type`, " +
            "(NOT EXISTS(SELECT * FROM article_view WHERE coid = column_view.id)) AS is_empty " +
            "FROM column_view WHERE id != -1 ORDER BY column_view.`index`")
    List<Column> selectAllExceptDefault();

    @Select("SELECT id, parent_id, CAST(name AS CHAR(1024)) AS name, `index`, `type` FROM column_view WHERE parent_id=#{coid}")
    List<Column> selectByFather(Integer coid);

    /** 新增栏目：不写主键，由数据库自增（避免与前端临时 id 冲突） */
    @Insert("INSERT INTO column_view (parent_id, name, `index`, type) VALUES (#{parent_id}, #{name}, #{index}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Column column_view);

    /** 全量重建栏目树时使用，保留原 id 与父子关系 */
    @Insert("INSERT INTO column_view (id, parent_id, name, `index`, type) VALUES (#{id}, #{parent_id}, #{name}, #{index}, #{type})")
    int insertWithId(Column column_view);

    @Update("UPDATE column_view SET `index` = #{index}, parent_id = #{parent_id} WHERE id = #{id}")
    void updateColumnById(Column column_view);

    @Delete("DELETE FROM column_view WHERE id = #{id}")
    void deleteColumn(Integer id);

    @Select("SELECT id, parent_id, CAST(name AS CHAR(1024)) AS name, `index`, `type` FROM column_view WHERE id = #{id}")
    Column selectByCid(Integer id);

    @Select("SELECT * FROM column_view WHERE `name` = #{cname}")
    Column selectByCname(String cname);

    @Select("SELECT id FROM column_view WHERE `name` = #{cname}")
    int getCidByCname(String cname);

    @Delete("DELETE FROM column_view")
    void deleteAll();

    @Update("UPDATE column_view SET name = #{name} WHERE id = #{id}")
    void updateNameById(Column column);

    @Update({"<script>",
            "UPDATE column_view SET `index` = `index` + #{increment} WHERE id IN ",
            "<foreach collection='columnList' item='column' open='(' separator=',' close=')'>",
            "#{column.id}",
            "</foreach>",
            "</script>"})
    void updateIndexById(List<Column> columnList, Integer increment);
}