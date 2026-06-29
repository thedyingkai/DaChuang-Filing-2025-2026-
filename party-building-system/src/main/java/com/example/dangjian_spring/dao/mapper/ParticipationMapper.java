package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Participation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ParticipationMapper extends BaseMapper<Participation> {
    String select_all_participation_s_sql =
            "SELECT user_id, test_paper_id, participation_time, test_record " +
            "FROM participation_o";
    @Select(select_all_participation_s_sql)
    List<Participation> allParticipation();

    String select_participation_s_byUser_sql =
            "SELECT user_id, test_paper_id, participation_time, test_record " +
            "FROM participation_o " +
            "WHERE user_id = #{id} " +
            "ORDER BY participation_time";
    @Select(select_participation_s_byUser_sql)
    List<Participation> allParticipationByUser(int id);

    String select_all_participation_s_byPaper_sql =
            "SELECT user_id, test_paper_id, participation_time, test_record " +
            "FROM participation_o " +
            "WHERE test_paper_id = #{id} " +
            "ORDER BY participation_time";
    @Select(select_all_participation_s_byPaper_sql)
    List<Participation> allParticipationByPaper(int id);

    String select_all_participation_and_uname_s_byPaper_sql =
            "SELECT participation_o.user_id, participation_o.test_paper_id, participation_o.participation_time, " +
                    "participation_o.test_record, COALESCE(user1.uname, user1.username) AS user_name " +
                    "FROM participation_o " +
                    "LEFT JOIN user1 ON participation_o.user_id = user1.uid " +
                    "WHERE participation_o.test_paper_id = #{id} " +
                    "ORDER BY participation_o.participation_time";
    @Select(select_all_participation_and_uname_s_byPaper_sql)
    List<Participation> allParticipationAndNameByPaper(int id);

    String select_all_participation_s_byPaperAndUser_sql =
            "SELECT user_id, test_paper_id, participation_time, test_record " +
                    "FROM participation_o " +
                    "WHERE test_paper_id = #{pid} AND user_id = #{uid} " +
                    "ORDER BY participation_time";
    @Select(select_all_participation_s_byPaperAndUser_sql)
    List<Participation> allParticipationByPaperAndUser(int pid, int uid);

    String insert_participation_sql =
            "INSERT INTO participation_o(user_id, test_paper_id, participation_time, test_record) " +
            "VALUES (#{user_id}, #{test_paper_id}, #{participation_time}, #{test_record})";
    @Insert(insert_participation_sql)
    int addParticipation(Participation participation);

    String update_participation_sql =
            "UPDATE participation_o " +
            "SET participation_time = #{participation_time}, test_record = #{test_record} " +
            "WHERE user_id = #{user_id} AND test_paper_id = #{test_paper_id}";
    @Update(update_participation_sql)
    int updateParticipation(Participation participation);



}
