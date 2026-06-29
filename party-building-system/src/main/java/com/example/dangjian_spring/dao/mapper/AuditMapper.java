package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Audit;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface AuditMapper extends BaseMapper<Audit> {

    @Select("SELECT * FROM audit_view")
    List<Audit> selectAll();

    @Insert("INSERT INTO audit_view (uid, srid, next, status, time) " +
            "VALUES (#{uid}, #{srid}, #{next}, #{status}, #{time})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int add(Audit audit);

    @Insert("INSERT INTO audit_view (uid, did, title, content, status, advice, send_time, reply_time) " +
            "VALUES (#{uid}, #{did}, #{title}, #{content}, #{status}, #{advice}, #{send_time}, #{reply_time})")
    int insert(Audit audit_view);

    @Update("UPDATE audit_view SET cname = #{cname}, permissions = #{permissions} WHERE id = #{id}")
    void updateAudit(Audit audit_view);

    @Delete("DELETE FROM audit_view WHERE id = #{id}")
    void deleteAudit(Integer id);

    @Select("SELECT * FROM audit_view WHERE id = #{id}")
    Audit selectByCid(Integer id);

    @Select("SELECT * FROM audit_view WHERE cname = #{cname}")
    Audit selectByCname(String cname);

    @Select("SELECT id FROM audit_view WHERE cname = #{cname}")
    int getCidByCname(String cname);
/*
    @Select("SELECT did AS id, draft_view.uid, uname editor_uname, aid, audit_view.content, save_time, " +
            "audit_view.send_time, audit_view.title, source, audit_view.status,reply_time " +
            "FROM audit_view JOIN draft_view ON audit_view.did = draft_view.id " +
            "JOIN user_view ON user_view.id = draft_view.uid " +
            "WHERE did IN (SELECT did FROM audit_view WHERE uid = #{uid})")
    List<Audit> selectAuditedByUid(Integer uid);
 */
    @Select("SELECT audit_view.id,audit_view.advice,audit_view.status,audit_view.time, " +
            "submit_view.content,submit_view.title,submit_view.source,user_view.uname as editor_uname " +
            "FROM audit_view JOIN submit_view ON audit_view.srid = submit_view.id " +
            "JOIN draft_view ON submit_view.did = draft_view.id " +
            "JOIN user_view ON draft_view.uid = user_view.id " +
            "WHERE audit_view.uid=#{uid} and (audit_view.status=2 or audit_view.status=3)")
    List<Audit> selectAuditedByUid(Integer uid);

    @Select("SELECT uid, advice, time, status, next " +
            "FROM audit_view WHERE srid = #{id}")
    List<Audit> selectBySubmitId(Integer id);

    @Select("SELECT audit_view.id, audit_view.uid, audit_view.srid, audit_view.next, audit_view.status, " +
            "audit_view.advice, audit_view.time, " +
            "submit_view.did, submit_view.title, submit_view.content, submit_view.source, " +
            "draft_view.coid, " +
            "user_view.uname, " +
            "COALESCE(CAST(column_view.name AS CHAR(512)), '') AS `column`, " +
            "COALESCE(draft_view.send_time, draft_view.save_time) AS save_time " +
            "FROM audit_view " +
            "JOIN submit_view ON audit_view.srid = submit_view.id " +
            "JOIN draft_view ON draft_view.id = submit_view.did " +
            "LEFT JOIN user_view ON user_view.id = draft_view.uid " +
            "LEFT JOIN column_view ON column_view.id = draft_view.coid " +
            "WHERE audit_view.uid = #{uid} AND audit_view.status = 1 " +
            "ORDER BY COALESCE(draft_view.send_time, draft_view.save_time) DESC")
    List<Audit> selectByUid(Integer uid);

    @Update("update audit_view set status=#{status},time=#{time},advice=#{advice} where id=#{id}")
    int updateById(Audit audit);

    @Update("update audit_view set status=1 where id=#{id}")
    int updateNextStatus(Integer id);


}