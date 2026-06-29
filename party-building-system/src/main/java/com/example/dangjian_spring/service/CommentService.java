package com.example.dangjian_spring.service;
import com.example.dangjian_spring.dao.mapper.CommentMapper;
import com.example.dangjian_spring.entity.Comment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;

@Service
@Transactional
public class CommentService {
    @Resource
    private CommentMapper commentMapper;

    public void submitcomment(Comment comment) {
        commentMapper.submitcomment(comment);
    }

    public void submitreply(Comment comment) {
        commentMapper.submitreply(comment);
    }

    public List<Comment> selectAllnoPASS() {
        return commentMapper.selectAllnoPASS();
    }

    public void audit(Comment comment) {
        commentMapper.audit(comment);
    }

    public List<Comment> selectbyarticle(Integer aid) {
        List<Comment> comments = commentMapper.selecttopcomment(aid);
        return buildtree(comments);
    }

    public List<Comment> selectbyuid(Integer uid) {
        return commentMapper.selectbyuid(uid);
    }

    public List<Comment> selectbyfatheruid(Integer id) {
        return commentMapper.selectbyfatheruid(id);
    }

    public void delete(Integer id) {
        commentMapper.delete(id);
    }

    public List<Comment> buildtree(List<Comment> comments) {
        for (Comment comment : comments) {
            Integer parentId = comment.getId();
            if (parentId == null) {
                continue; // 如果 ID 为 null，跳过当前评论
            }

            List<Comment> children = commentMapper.selectbyfatherid(parentId);
            if (!children.isEmpty()) {
                comment.setChildren(buildtree(children));
            }
        }
        return comments;
    }

}