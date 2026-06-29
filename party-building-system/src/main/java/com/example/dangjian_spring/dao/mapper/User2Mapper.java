package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.User2;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface User2Mapper extends BaseMapper<User2> {

    // 列出所有用户的基本信息以及权限信息
    String select_all_sql =
            "SELECT user_id, user_name, password, points, user2.role_id, role_name, role_permissions " +
            "FROM User2 JOIN Role ON User2.role_id = Role.role_id " +
            "order by user_id asc";
    @Select(select_all_sql)
    List<User2> listAllUsers();

    // 增加用户
    String add_user_sql =
            "INSERT INTO User2(user_id ,user_name, password, points, role_id) " +
            "values(#{user_id}, #{user_name}, #{password}, #{points}, #{role_id})";
    @Insert(add_user_sql)
    int addUser(User2 u);

    // 更新用户信息
    String update_user_sql =
            "UPDATE User2 set user_name = #{user_name}, password = #{password}, points = #{points}, role_id = #{role_id} " +
            "WHERE user_id = #{user_id}";
    @Update(update_user_sql)
    int updateUserInfo(User2 u);

    // 通过id删除用户信息
    String delete_user_sql =
            "DELETE FROM User2 WHERE user_id = #{id}";
    @Delete(delete_user_sql)
    int deleteUserById(int id);


    /** JWT 中存的是 User2.user_id；须从 User2+Role 解析，与 {@link #selectUserByName(String)} 一致 */
    String select_user_sql =
            "SELECT user_id, user_name, password, points, User2.role_id, role_name, role_permissions " +
                    "FROM User2 JOIN Role ON User2.role_id = Role.role_id " +
                    "WHERE user_id = #{id}";
    @Select(select_user_sql)
    User2 selectUserById(int id);

    /** 新注册用户仅写入 User2 时，避免与已有 user1.uid / User2.user_id 主键冲突 */
    @Select("SELECT GREATEST(IFNULL((SELECT MAX(uid) FROM user1), 0), IFNULL((SELECT MAX(user_id) FROM User2), 0)) + 1")
    int nextAvailableUser2Id();

    // 通过name寻找用户
    String select_user_byName_sql =
            "SELECT user_id, user_name, password, points, user2.role_id, role_name, role_permissions " +
            "FROM User2 JOIN Role ON User2.role_id = Role.role_id " +
            "WHERE user_name = #{name}";
    @Select(select_user_byName_sql)
    User2 selectUserByName(String name);

    // 模糊查询
    String search_users_byKeyword =
            "SELECT user_id, user_name, password, points, user2.role_id, role_name, role_permissions " +
            "FROM User2 JOIN Role ON User2.role_id = Role.role_id " +
            "WHERE user_name LIKE #{keyword}";
    @Select(search_users_byKeyword)
    List<User2> searchUsersByKeyword(String keyword);





}
