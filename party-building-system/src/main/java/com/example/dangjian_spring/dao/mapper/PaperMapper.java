package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.TestPaper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PaperMapper extends BaseMapper<TestPaper> {
    String select_all_papers_sql =
            "SELECT test_paper_id, paper_description, grouping_method, points_reward, create_date, deadline " +
            "FROM testpaper_o " +
            "ORDER BY test_paper_id ASC ";

    @Select(select_all_papers_sql)
    List<TestPaper> allPapers();

    String search_papers_byDes_sql =
            "SELECT test_paper_id, paper_description, grouping_method, points_reward, create_date, deadline " +
            "FROM testpaper_o " +
            "WHERE paper_description LIKE #{keyword} ";
    @Select(search_papers_byDes_sql)
    List<TestPaper> searchPaperByDes(String keyword);

    String search_papers_byMethod_sql =
            "SELECT test_paper_id, paper_description, grouping_method, points_reward, create_date, deadline " +
            "FROM testpaper_o " +
            "WHERE grouping_method LIKE #{keyword}";
    @Select(search_papers_byMethod_sql)
    List<TestPaper> searchPaperByMethod(String keyword);

    String select_paper_byId_sql =
            "SELECT test_paper_id, paper_description, grouping_method, points_reward, create_date, deadline " +
            "FROM testpaper_o " +
            "WHERE test_paper_id = #{id}";
    @Select(select_paper_byId_sql)
    TestPaper selectPaperById(int id);

    String select_paper_byDes_sql =
            "SELECT test_paper_id, paper_description, grouping_method, points_reward, create_date, deadline " +
                    "FROM testpaper_o " +
                    "WHERE paper_description = #{description}";
    @Select(select_paper_byDes_sql)
    TestPaper selectPaperByDes(String description);

    /** 由数据库生成 test_paper_id（AUTO_INCREMENT），避免与手写 ID 冲突 */
    @Insert(
            "INSERT INTO testpaper_o(paper_description, grouping_method, points_reward, create_date, deadline) "
                    + "VALUES (#{paper_description}, #{grouping_method}, #{points_reward}, #{create_date}, #{deadline})")
    @Options(useGeneratedKeys = true, keyProperty = "test_paper_id", keyColumn = "test_paper_id")
    int addPaper(TestPaper paper);

    String update_paper_sql =
            "UPDATE testpaper_o " +
            "SET paper_description = #{paper_description}, grouping_method = #{grouping_method}, " +
            "points_reward = #{points_reward}, create_date = #{create_date}, deadline = #{deadline} " +
            "WHERE test_paper_id = #{test_paper_id}";

    @Update(update_paper_sql)
    int updatePaper(TestPaper paper);

    String deletePaper_sql =
            "DELETE FROM testpaper_o WHERE test_paper_id = #{id}";
    @Delete(deletePaper_sql)
    int deletePaper(int id);
}
