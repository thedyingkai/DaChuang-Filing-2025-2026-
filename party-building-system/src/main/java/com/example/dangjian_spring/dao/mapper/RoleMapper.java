package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    String select_all_roles_sql =
            "SELECT role_id, role_name, role_permissions " +
            "FROM Role " +
            "order by role_id asc";
    @Select(select_all_roles_sql)
    List<Role> listAllRoles();

    String select_role_byName_sql =
            "SELECT role_id, role_name, role_permissions " +
            "FROM Role " +
            "WHERE role_name = #{role_name}";
    @Select(select_role_byName_sql)
    Role selectRoleByName(String role_name);

    String search_roles_byKeyword_sql =
            "SELECT role_id, role_name, role_permissions FROM Role WHERE role_name LIKE #{keyword}";
    @Select(search_roles_byKeyword_sql)
    List<Role> searchRolesByKeyword(String keyword);

    String update_role_sql =
            "UPDATE Role set role_name = #{role_name}, role_permissions = #{role_permissions} " +
            "WHERE role_id = #{role_id}";
    @Update(update_role_sql)
    int updateRole(Role role);

    String add_role_sql =
            "INSERT INTO Role(role_id, role_name, role_permissions) " +
            "VALUES(#{role_id}, #{role_name}, #{role_permissions})";
    @Insert(add_role_sql)
    int addRole(Role role);

    String delete_role_byId_sql =
            "DELETE FROM Role WHERE role_id = #{id}";
    @Delete(delete_role_byId_sql)
    int deleteRoleById(int id);

}
