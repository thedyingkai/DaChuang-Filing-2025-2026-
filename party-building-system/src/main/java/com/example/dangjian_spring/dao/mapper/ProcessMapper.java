package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Process;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProcessMapper extends BaseMapper<Process> {

    @Select("SELECT p.id, p.last, p.next, a.uid AS uid, u.uname AS uname " +
            "FROM process_view p " +
            "LEFT JOIN process_auditor_view a ON a.pid = p.id " +
            "LEFT JOIN user_view u ON u.id = a.uid " +
            "ORDER BY p.id")
    List<Process> selectAll();

    /**
     * 审核员在 process_auditor_view；勿使用 process_view.* 与 a.uid 同查，避免两列同名 uid 时映射为 null。
     */
    @Select("SELECT p.id, p.last, p.next, a.uid AS uid, u.uname AS uname " +
            "FROM process_view p " +
            "LEFT JOIN process_auditor_view a ON a.pid = p.id " +
            "LEFT JOIN user_view u ON u.id = a.uid " +
            "WHERE p.id = #{id}")
    Process selectById(Integer id);

    @Select("SELECT * from process_view where next=#{id}")
    Process selectByNext(Integer id);

    @Insert("INSERT INTO process_view (`last`) VALUES (1)")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int add(Process holder);

    @Update("UPDATE process_view set last=0,next=#{next} where id=#{id}")
    void removeLast(Process process);

    @Update("UPDATE process_view set last=1,next=NULL where id=#{id}")
    void setLast(Integer id);


    @Update("UPDATE process_view set uid=#{uid} where id=#{id}")
    void updateAuditor(Process process);

    @Delete("DELETE FROM process_view where id=#{id}")
    void delete(Integer id);

}
