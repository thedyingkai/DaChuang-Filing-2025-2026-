package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface User1Mapper extends BaseMapper<User> {

    @Select("SELECT * FROM user_view JOIN chara ON user_view.cid = chara.cid order by id asc")
    List<User> selectAll();

    @Insert("insert into user_view (uname, psw, cid) values(#{uname}, #{psw}, #{cid})")
    int insert(User user_view);

    @Update("UPDATE user_view set cid = #{cid}, uname = #{uname}, psw = #{psw}, points = #{points} WHERE id = #{id}")
    void updateUser(User user_view);

    @Delete("DELETE FROM user_view WHERE id = #{id}")
    void deleteUser(Integer id);

    @Select("SELECT user_view.*,chara.permissions FROM user_view JOIN chara ON user_view.cid = chara.cid WHERE id = #{id}")
    User selectByUid(Integer id);

    //    @Select("SELECT user_view.*, chara.cname, chara.permissions FROM user_view JOIN chara ON user_view.cid = chara.cid WHERE uname = #{uname}")
    @Select("SELECT user_view.id, user_view.gid, deid, uname, points, psw, avatar, username, permissions, " +
            "branch_view.name AS branchName,branch_view.bid, group_view.name AS groupName, " +
            "department_view.name AS departName, sector_view.name AS sectorName " +
            "FROM user_view " +
            "LEFT JOIN chara ON user_view.cid = chara.cid " +
            "LEFT JOIN group_view ON group_view.gid = user_view.gid " +
            "LEFT JOIN branch_view ON branch_view.bid = group_view.bid " +
            "LEFT JOIN department_view ON department_view.id = user_view.deid " +
            "LEFT JOIN sector_view ON sector_view.id = department_view.seid " +
            "WHERE uname = #{uname}")
    User selectByUname(String uname);

    @Select("SELECT * FROM user_view WHERE uname = #{uname} AND cid = #{cid}")
    User selectByMore(@Param("uname") String uname, @Param("cid") Integer cid);

    @Select("SELECT * FROM user_view WHERE uname LIKE CONCAT('%', #{uname}, '%') AND cid = #{cid}")
    List<User> selectByMoreLike(@Param("uname") String uname, @Param("cid") Integer cid);

    @Select("SELECT * FROM user_view WHERE uname LIKE CONCAT('%', #{uname}, '%') AND cid = #{cid} " +
            "ORDER BY id asc LIMIT #{pageNum}, #{pageSize}")
    List<User> selectByMoreLikePage(@Param("pageNum") Integer skipNum, @Param("pageSize") Integer pageSize,
                                    @Param("uname") String uname, @Param("cid") Integer cid);

    @Select("SELECT COUNT(id) FROM user_view WHERE uname LIKE CONCAT('%', #{uname}, '%') AND cid = #{cid} " +
            "ORDER BY id asc")
    int selectCountByPage(@Param("uname") String uname, @Param("cid") Integer cid);

    @Select("SELECT id, uname, points, psw, cname FROM user_view JOIN chara ON user_view.cid = chara.cid order by id asc")
    List<User> selectAllToExport();

    @Select("SELECT * FROM user_view where deid=#{deid}")
    List<User> selectByDeid(Integer deid);

    @Select("select * from user_view where gid=#{gid}")
    List<User> selectByGid(Integer gid);

    @Select("SELECT * from user_view where deid IS NULL or deid =-1")
    List<User> selectByDeidIsNull();

    @Select("SELECT * from user_view where gid IS NULL or gid =-1")
    List<User> selectByGidIsNull();

    @Select("SELECT user_view.* from user_view,group_view,branch_view "
            + "where user_view.gid=group_view.gid AND group_view.bid=branch_view.bid AND branch_view.bid=#{bid}")
    List<User> selectByBranch(Integer bid);

    @Select("SELECT user_view.* from user_view,chara_view "
            + "where user_view.cid=chara_view.id AND SUBSTRING(permissions FROM 3 FOR 1) = '1'")
    List<User> selectAuditor();

    @Update("UPDATE user_view set deid=#{deid} where id=#{id}")
    void updateDeid(User user);

    @Update("UPDATE user_view set gid=#{gid} where id=#{id}")
    void updateGid(Integer id, Integer gid);

    @Update("UPDATE user_view set gid=-1 where id=#{id}")
    void deleteGid(Integer id);


    @Select("SELECT user_view.id, COALESCE(user_view.username, user_view.uname) AS username " +
            "FROM user_view " +
            "LEFT JOIN group_view ON user_view.gid = group_view.gid " +
            "WHERE group_view.bid = (SELECT bid FROM group_view WHERE gid = #{gid})")
    List<Map<String, Object>> selectBranchByGid(Integer gid);

    @Update("UPDATE user_view set avatar = #{avatar} where id = #{id}")
    void updateAvatar(User user);

    @Update("UPDATE user_view set username = #{username} where id = #{id}")
    void updateUsername(User user);

    @Update("UPDATE user_view set uname = #{uname}, psw = #{psw} where id = #{id}")
    void updateAccount(User user);


}
