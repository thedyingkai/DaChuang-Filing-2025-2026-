package com.example.dangjian_spring.dao.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dangjian_spring.entity.Article;
import com.example.dangjian_spring.entity.Audit;
import com.example.dangjian_spring.entity.Comment;
import com.example.dangjian_spring.entity.Draft;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment>{

    @Insert("insert into comments_view (uid, aid, content, send_time) " +
            "values(#{uid}, #{aid}, #{content},  #{send_time})")
    void  submitcomment(Comment comment);

    @Insert("insert into comments_view (uid, aid, content, send_time,parent_id) " +
            "values(#{uid}, #{aid}, #{content},  #{send_time}, #{parent_id})")
    void  submitreply(Comment comment);

    @Select("SELECT article_view.title,comments_view.id,comments_view.uid, comments_view.aid, parent_id, comments_view.content, send_time,uname " +
            "FROM comments_view JOIN user_view ON user_view.id = comments_view.uid JOIN article_view ON article_view.id = comments_view.aid WHERE comments_view.status=0 order by comments_view.id asc")
    List<Comment> selectAllnoPASS();

    @Update("UPDATE comments_view set audit_uid = #{audit_uid},status=#{status}  WHERE id = #{id}")
    void audit(Comment comment);

    @Select("SELECT comments_view.*,user_view.uname,user_view.avatar FROM comments_view,user_view WHERE comments_view.uid=user_view.id AND comments_view.aid=#{aid} AND comments_view.status=2")
    List<Comment> selectbyarticle(Integer aid);

    @Select("SELECT comments_view.*,article_view.title FROM comments_view,article_view WHERE comments_view.aid=article_view.id AND comments_view.uid=#{uid}")
    List<Comment> selectbyuid(Integer uid);

    @Select("SELECT c1.*,user_view.uname,user_view.avatar FROM comments_view c1,comments_view c2,user_view WHERE c1.uid=user_view.id AND c1.parent_id=c2.id AND c2.uid=#{id} AND c1.status=2")
    List<Comment> selectbyfatheruid(Integer id);

    @Select("SELECT comments_view.*,user_view.uname,user_view.avatar FROM comments_view,user_view"+
            " WHERE comments_view.uid=user_view.id AND comments_view.aid=#{aid} AND comments_view.status=2 AND comments_view.parent_id IS NULL order by comments_view.send_time")
    List<Comment> selecttopcomment(Integer aid);

    @Select("SELECT comments_view.*,user_view.uname,user_view.avatar FROM comments_view,user_view"+
            " WHERE comments_view.uid=user_view.id  AND comments_view.status=2 AND comments_view.parent_id=#{id}")
    List<Comment> selectbyfatherid(Integer id);

    @Delete("DELETE FROM comments_view WHERE id = #{id}")
    void delete(Integer id);


}
