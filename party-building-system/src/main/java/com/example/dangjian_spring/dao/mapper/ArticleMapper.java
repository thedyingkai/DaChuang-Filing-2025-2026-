package com.example.dangjian_spring.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Article;
import com.example.dangjian_spring.entity.Audit;
import com.example.dangjian_spring.entity.Draft;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT article_view.id, coid, content, publish_time," +
            " title, source, `show`, column_view.name AS `column` " +
            "FROM article_view JOIN column_view ON article_view.coid = column_view.id order by article_view.id asc")
    List<Article> selectAll();

    /**
     * 与 init 脚本一致：多数库中 article_view 无 uid 列（作者可由业务侧关联草稿等表），须包含 show。
     * useGeneratedKeys 回填自增 ID，使 Dify 等调用方能获取到新文章的 ID。
     */
    @Insert("INSERT INTO article_view (coid, content, title, publish_time, source, `show`) " +
            "VALUES (#{coid}, #{content}, #{title}, #{publish_time}, #{source}, #{show})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Article article_view);

    @Select("SELECT article_view.id, coid, DATE_FORMAT(publish_time, '%Y-%m-%d') AS publish_time, " +
            "title, source, `show` AS `column` " +
            "FROM article_view Where coid=#{id} order by article_view.id desc")
    List<Article> selectByCoid(Integer id);

    @Select("SELECT article_view.id, coid, content, publish_time," +
            " title, source, `show` AS `column` " +
            "FROM article_view Where id=#{id} order by article_view.id ")
    Article selectByid(Integer id);

    @Delete("DELETE FROM article_view WHERE id = #{id}")
    void deletebyid(Integer id);

    @Update("Update article_view SET coid = #{coid} WHERE id = #{id}")
    void updateCoid(Integer coid, Integer id);

    @Update("<script>UPDATE article_view SET coid = #{coid} WHERE id IN " +
            "<foreach collection=\"articleIdList\" item=\"articleId\" open=\"(\" separator=\",\" close=\")\">" +
            "#{articleId}" +
            "</foreach></script>")
    void batchUpdateCoid(@Param("articleIdList") List<Integer> articleIdList, @Param("coid") Integer coid);

    @Select("SELECT article_view.* From article_view " +
            "LEFT JOIN user_view ON article_view.uid=user_view.id " +
            "WHERE user_view.id=#{id}")
    List<Article> selectByUid(Integer id);

    @Select("SELECT article_view.id,article_view.publish_time,branch_view.bid as bid,branch_view.name From article_view " +
            "LEFT JOIN user_view ON article_view.uid=user_view.id " +
            "LEFT JOIN group_view ON group_view.gid=user_view.gid " +
            "LEFT JOIN branch_view ON branch_view.bid=group_view.bid " +
            "ORDER BY branch_view.bid")
    List<Article> selectByBranch();

    /**
     * 按标题模糊搜索文章（Dify工作流智能检索用）
     */
    @Select("SELECT article_view.id, coid, content, publish_time, " +
            "title, source, `show`, column_view.name AS `column` " +
            "FROM article_view JOIN column_view ON article_view.coid = column_view.id " +
            "WHERE article_view.title LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY article_view.id DESC")
    List<Article> selectByTitle(String keyword);

    // ==================== Dify 数据问答新增方法 ====================

    /**
     * 按时间范围统计文章数（Dify数据问答用）
     */
    @Select("SELECT COUNT(*) FROM article_view WHERE publish_time >= #{startDate} AND publish_time <= #{endDate}")
    int countByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 按时间范围和栏目统计文章数（Dify数据问答用）
     */
    @Select("SELECT column_view.name, COUNT(article_view.id) AS cnt " +
            "FROM article_view JOIN column_view ON article_view.coid = column_view.id " +
            "WHERE article_view.publish_time >= #{startDate} AND article_view.publish_time <= #{endDate} " +
            "GROUP BY column_view.name ORDER BY cnt DESC")
    List<Map<String, Object>> countByColumnAndDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 按时间范围查询文章列表（Dify工作流内过滤用）
     */
    @Select("SELECT article_view.id, coid, content, publish_time, " +
            "title, source, `show`, column_view.name AS `column` " +
            "FROM article_view JOIN column_view ON article_view.coid = column_view.id " +
            "WHERE article_view.publish_time >= #{startDate} AND article_view.publish_time <= #{endDate} " +
            "ORDER BY article_view.publish_time DESC")
    List<Article> selectByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
}