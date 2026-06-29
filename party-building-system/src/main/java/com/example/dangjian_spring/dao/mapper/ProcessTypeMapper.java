package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.ProcessType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProcessTypeMapper extends BaseMapper<ProcessType> {
    @Insert("INSERT INTO processtype_view(pid,name,number,type) values (#{pid},#{name},1,0)")
    int insert(ProcessType processType);

    @Select("SELECT pt.id, pt.pid, pt.name, pt.number, pt.type, " +
            "GROUP_CONCAT(DISTINCT CAST(cv.name AS CHAR(512)) ORDER BY cv.name SEPARATOR '、') AS co_name " +
            "FROM processtype_view pt " +
            "LEFT JOIN processtype_to_column_view ptc ON pt.id = ptc.ptid " +
            "LEFT JOIN column_view cv ON cv.id = ptc.coid " +
            "GROUP BY pt.id, pt.pid, pt.name, pt.number, pt.type " +
            "ORDER BY pt.id")
    List<ProcessType> selectall();

    @Select("SELECT pt.id, pt.pid, pt.name, pt.number, pt.type, " +
            "GROUP_CONCAT(DISTINCT CAST(cv.name AS CHAR(512)) ORDER BY cv.name SEPARATOR '、') AS co_name " +
            "FROM processtype_view pt " +
            "LEFT JOIN processtype_to_column_view ptc ON pt.id = ptc.ptid AND ptc.bid = #{bid} " +
            "LEFT JOIN column_view cv ON cv.id = ptc.coid " +
            "GROUP BY pt.id, pt.pid, pt.name, pt.number, pt.type " +
            "ORDER BY pt.id")
    List<ProcessType> selectByBid(Integer bid);

    @Select("SELECT processtype_view.*,column_view.id as coid,column_view.name as co_name FROM processtype_view  " +
            "LEFT JOIN processtype_to_column_view ON processtype_view.id=processtype_to_column_view.ptid " +
            "LEFT JOIN column_view ON processtype_to_column_view.coid = column_view.id " +
            "where processtype_view.id=#{id}")
    ProcessType selectById(Integer id);


    @Select("SELECT processtype_view.*,column_view.id as coid,column_view.name as co_name FROM processtype_view  " +
            "LEFT JOIN processtype_to_column_view ON processtype_view.id=processtype_to_column_view.ptid " +
            "LEFT JOIN column_view ON processtype_to_column_view.coid = column_view.id " +
            "where type=1")
    ProcessType selectByType();

    @Select("SELECT processtype_view.* FROM processtype_view  " +
            "LEFT JOIN processtype_to_column_view ON processtype_view.id=processtype_to_column_view.ptid " +
            "where processtype_to_column_view.coid=#{coid} and processtype_to_column_view.bid=#{bid}")
    ProcessType selectByCoid(Integer coid,Integer bid);

    /**
     * 仅按栏目 id 匹配流程（bid 为空或与库不一致时的回退）。
     */
    @Select("SELECT processtype_view.*, column_view.id AS coid, CAST(column_view.name AS CHAR(1024)) AS co_name " +
            "FROM processtype_view " +
            "INNER JOIN processtype_to_column_view ON processtype_view.id = processtype_to_column_view.ptid " +
            "LEFT JOIN column_view ON column_view.id = processtype_to_column_view.coid " +
            "WHERE processtype_to_column_view.coid = #{coid} " +
            "ORDER BY processtype_view.id ASC LIMIT 1")
    ProcessType selectFirstByCoid(Integer coid);

    @Delete("DELETE FROM processtype_view WHERE id = #{id}")
    int delete(Integer id);

    /**
     * 删除流程节点前须解除外键：processtype.pid → process。
     * 按方案 id 清空头指针（删除整条链前调用）。
     */
    @Update("UPDATE processtype_view SET pid = NULL WHERE id = #{ptid}")
    int clearPidByPtid(Integer ptid);

    /**
     * 删除作为链头的流程节点时，把头指针改到后继节点；无后继则置空。
     */
    @Update("UPDATE processtype_view SET pid = #{newPid} WHERE pid = #{oldPid}")
    int rewirePidHead(Integer oldPid, Integer newPid);

    @Update("UPDATE processtype_view SET pid = NULL WHERE pid = #{oldPid}")
    int clearPidByProcessHead(Integer oldPid);

    @Update("UPDATE processtype_view set number=#{number} where id=#{id}")
    int updateNumber(ProcessType processType);

    @Update("UPDATE processtype_view set type=0")
    int typesetzero();

    @Update("UPDATE processtype_view set type=1 where id=#{id}")
    int typesetone(Integer id);
/*
    @Update("UPDATE processtype_view set coid=#{coid} where id=#{id}")
    int updateCoid(ProcessType processType);
 */


}
