package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Draft;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DraftMapper extends BaseMapper<Draft> {

    @Select("SELECT draft_view.id, uid, coid, content, save_time," +
            " title, source, status, send_time, column_view.name AS `column` " +
            "FROM draft_view JOIN column_view ON draft_view.coid = column_view.id order by draft_view.id asc")
    List<Draft> selectAll();

    @Insert("insert into draft_view (uid, coid, content, title, save_time, source) " +
            "values(#{uid}, #{coid}, #{content}, #{title}, #{save_time}, #{source})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Draft draft_view);

    @Update("UPDATE draft_view set coid = #{coid}, title = #{title}, content = #{content}, save_time = #{save_time}, " +
            "source = #{source} WHERE id = #{id}")
    void updateDraft(Draft draft_view);

    @Delete("DELETE FROM draft_view WHERE id = #{id}")
    void deleteDraft(Integer id);

    @Select("SELECT submit_view.*,draft_view.coid,draft_view.uid FROM submit_view " +
            "LEFT JOIN draft_view ON draft_view.id=submit_view.did WHERE submit_view.id = #{id}")
    Draft selectById(Integer id);

    @Select("SELECT * FROM draft_view WHERE uname = #{uname}")
    Draft selectByTitle(String uname);

    @Select("SELECT * FROM draft_view WHERE uname = #{uname} AND cid = #{cid}")
    Draft selectByMore(@Param("uname") String uname, @Param("cid") Integer cid);

    @Select("SELECT * FROM draft_view WHERE uname LIKE CONCAT('%', #{uname}, '%') AND cid = #{cid}")
    List<Draft> selectByMoreLike(@Param("uname") String uname, @Param("cid") Integer cid);

    @Select("SELECT * FROM draft_view WHERE uname LIKE CONCAT('%', #{uname}, '%') AND cid = #{cid} " +
            "ORDER BY id asc LIMIT #{pageNum}, #{pageSize}")
    List<Draft> selectByMoreLikePage(@Param("pageNum") Integer skipNum, @Param("pageSize") Integer pageSize,
                                     @Param("uname") String uname, @Param("cid") Integer cid);

    @Select("SELECT COUNT(id) FROM draft_view WHERE uname LIKE CONCAT('%', #{uname}, '%') AND cid = #{cid} " +
            "ORDER BY id asc")
    int selectCountByPage(@Param("uname") String uname, @Param("cid") Integer cid);

    @Select("SELECT * FROM draft_view WHERE uid = #{uid} ORDER BY save_time DESC")
    List<Draft> selectByUid(Integer uid);

    @Select("SELECT submit_view.id as srid, uid,  coid, submit_view.content, " +
            "save_time, submit_view.title, submit_view.source, submit_view.status, send_time,draft_view.id " +
            "FROM submit_view JOIN draft_view ON submit_view.did = draft_view.id " +
            "JOIN column_view ON draft_view.coid = column_view.id " +
            "WHERE uid = #{uid} ORDER BY save_time DESC")
    List<Draft> selectSubByUid(Integer uid);

    @Insert("INSERT INTO draft_view (uid, coid, content, title, save_time, send_time, status, source) " +
            "VALUES (#{uid}, #{coid}, #{content}, #{title}, #{save_time}, #{send_time}, 1, #{source})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertAndSubmit(Draft draft_view);

    @Select("SELECT draft_view.id, uid, coid, draft_view.content, " +
            "save_time, draft_view.title, draft_view.source, draft_view.status, send_time " +
            "FROM draft_view JOIN column_view ON draft_view.coid = column_view.id " +
            "WHERE uid = #{uid} AND status = 0 ORDER BY save_time DESC")
    List<Draft> selectLocalByUid(Integer uid);

    @Update("UPDATE draft_view set coid = #{coid}, title = #{title}, content = #{content}, save_time = #{save_time}, " +
            "send_time = #{send_time}, status=1, source = #{source} WHERE id = #{id}")
    void submit(Draft draft_view);

    @Select("SELECT draft_view.id, uid, uname, content, save_time, send_time, " +
            "title, source, status, coid, column_view.name AS `column` " +
            "FROM draft_view JOIN user_view ON draft_view.uid = user_view.id " +
            "JOIN column_view ON column_view.id = draft_view.coid " +
            "WHERE status = 1 OR status = 4 ORDER BY save_time DESC")
    List<Draft> selectAllToAudit();

    @Select("SELECT send_time FROM draft_view WHERE id = #{did}")
    String selectSendTimeById(Integer did);

    @Update("UPDATE draft_view set status = #{status} WHERE id = #{did}")
    void updateStatusById(Integer did, Integer status);

    @Update("UPDATE draft_view set status = 4 WHERE id = #{id}")
    void lockDraft(Integer id);

    @Update("UPDATE draft_view set status = 1 WHERE id = #{id}")
    void unlockDraft(Integer id);

    @Select("SELECT status FROM draft_view WHERE id = #{id}")
    Integer checkStatus(Integer id);

    /**
     * 按草稿主键读取 {@code draft_view} 一行（勿与按 submit id 查询的 {@link #selectById(Integer)} 混淆）。
     */
    @Select("SELECT id, uid, coid, content, save_time, title, source, status, send_time FROM draft_view WHERE id = #{id}")
    Draft selectDraftByPk(@Param("id") Integer id);

    @Select("SELECT draft_view.id, uid, uname, content, save_time, send_time, title, source, status" +
            " FROM draft_view JOIN user_view ON draft_view.uid = user_view.id " +
            " WHERE draft_view.id IN (SELECT did FROM audit_view WHERE uid = #{uid})")
    List<Draft> selectAuditedByUid(Integer uid);
}
