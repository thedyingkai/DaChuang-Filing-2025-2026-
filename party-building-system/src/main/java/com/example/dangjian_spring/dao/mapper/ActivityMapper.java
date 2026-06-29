package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Activity;
import com.example.dangjian_spring.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    @Insert("insert into activity_view (uid, name, type, time, content, cover_image) " +
            "values(#{uid}, #{name}, #{type}, #{time}, #{content}, #{cover_image})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void add(Activity activity);

    @Update("update activity_view set name=#{name}, uid = #{uid}, time = #{time}, content = #{content}, type = #{type} " +
            "where id=#{id}")
    void update(Activity activity);

    @Select("SELECT activity_view.id, activity_view.uid, activity_view.type, activity_view.name, " +
            "CAST(activity_view.time AS DATE) AS time, user_view.uname " +
            "FROM activity_view " +
            "LEFT JOIN user_view ON activity_view.uid = user_view.id " +
            "ORDER BY activity_view.id DESC")
    List<Activity> selectAll();

    @Delete("DELETE from activity_view where id=#{id}")
    void delete(int id);

    @Update("update activity_view set name=#{name} where id=#{id}")
    void rename(Activity activity);

    @Select("SELECT activity_view.id, activity_view.uid, activity_view.type, activity_view.name, CAST(activity_view.time AS DATE) AS time, user_view.uname " +
            "FROM activity_view, user_view " +
            "WHERE activity_view.uid = user_view.id AND activity_view.id <> -1")
    List<Activity> selectAllExceptDefault();

    @Select("SELECT activity_view.id, activity_view.uid, activity_view.name, time, content, type, uname, cover_image " +
            "FROM activity_view " +
            "JOIN user_view ON activity_view.uid = user_view.id " +
            "JOIN group_view ON group_view.gid = user_view.gid " +
            "WHERE type = #{type} AND group_view.bid = " +
            "(SELECT bid " +
            "FROM user_view " +
            "LEFT JOIN group_view ON user_view.gid = group_view.gid " +
            "WHERE user_view.id = #{uid})")
    List<Activity> selectByTypeId(Integer type, Integer uid);

    @Select("SELECT " +
            "activity_view.id, " +
            "activity_view.uid, " +
            "activity_view.name, " +
            "activity_view.time, " +
            "activity_view.content, " +
            "activity_view.type, " +
            "COALESCE(user_view.username, user_view.uname) AS username, " +
            "activity_view.cover_image, " +
            "(SELECT COUNT(*) FROM participate WHERE acid = activity_view.id AND `type` = 0) AS participateCount, " +
            "(SELECT COUNT(*) FROM participate WHERE acid = activity_view.id AND `type` = 1) AS sickLeaveCount, " +
            "(SELECT COUNT(*) FROM participate WHERE acid = activity_view.id AND `type` = 2) AS personalLeaveCount, " +
            "(SELECT COUNT(*) FROM participate WHERE acid = activity_view.id AND `type` = 3) AS otherCount " +
            "FROM activity_view " +
            "JOIN user_view ON activity_view.uid = user_view.id " +
            "WHERE activity_view.id = #{id}")
    Activity selectById(Integer id);

    @Insert("INSERT INTO absent_view(uid, acid) VALUES( #{uid}, #{acid})")
    void participate(Integer acid, Integer uid);

    @Select("SELECT * FROM user_view JOIN absent_view ON absent_view.uid = user_view.id " +
            "WHERE absent_view.acid = #{id}")
    List<User> selectMembers(Integer id);

    @Delete("DELETE FROM absent_view WHERE acid = #{acid}")
    void clearParticipation(Integer acid);

    @Update("update activity_view set cover_image = #{cover_image} where id=#{id}")
    void updateCoverImage(Activity activity);

    // ==================== Dify 数据问答新增方法 ====================

    /**
     * 按时间范围统计活动数（Dify数据问答用）
     */
    @Select("SELECT COUNT(*) FROM activity_view WHERE time >= #{startDate} AND time <= #{endDate}")
    int countByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 按时间范围和活动类型统计（Dify数据问答用）
     * type: 0=主题党日, 1=党员大会, 2=党课, 3=组织生活会 等（以实际业务为准）
     */
    @Select("SELECT type, COUNT(*) AS cnt FROM activity_view " +
            "WHERE time >= #{startDate} AND time <= #{endDate} " +
            "GROUP BY type ORDER BY cnt DESC")
    List<Map<String, Object>> countByTypeAndDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 按时间范围查询活动列表（Dify工作流内过滤用）
     */
    @Select("SELECT activity_view.id, activity_view.uid, activity_view.type, activity_view.name, " +
            "CAST(activity_view.time AS DATE) AS time, user_view.uname " +
            "FROM activity_view, user_view " +
            "WHERE activity_view.uid = user_view.id " +
            "AND activity_view.time >= #{startDate} AND activity_view.time <= #{endDate} " +
            "ORDER BY activity_view.time DESC")
    List<Activity> selectByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
}