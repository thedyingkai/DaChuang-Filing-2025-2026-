package com.example.dangjian_spring.Controller;


import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Comment;
import com.example.dangjian_spring.entity.User;
import com.example.dangjian_spring.service.CommentService;
import com.example.dangjian_spring.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CommentService commentService;

    @PostMapping("/submitcomment")
    public Result submitcomment(@RequestBody Comment comment) {
        User currentUser = TokenUtils.getCurrentUser1();
        commentService.submitcomment(comment);
        return Result.success();
    }

    @PostMapping("/submitreply")
    public Result submitreply(@RequestBody Comment comment) {
        commentService.submitreply(comment);
        return Result.success();
    }

    @GetMapping("/selectbyuid/{uid}")
    public Result selectbyuid(@PathVariable Integer uid) {
        List<Comment> commentList = commentService.selectbyuid(uid);
        return Result.success(commentList);
    }

    @GetMapping("/selectbyfatheruid/{id}")
    public Result selectbyfatheruid(@PathVariable Integer id) {
        List<Comment> commentList = commentService.selectbyfatheruid(id);
        return Result.success(commentList);
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Integer id) {
        commentService.delete(id);
        return Result.success();
    }

    @GetMapping("/selectallnopass")
    public Result selectallnopass() {
        List<Comment> commentslist = commentService.selectAllnoPASS();
        return Result.success(commentslist);
    }

    @AuthAccess
    @GetMapping("/selectbyarticle/{aid}")
    public Result selectbyarticle(@PathVariable Integer aid) {
        List<Comment> commentslist = commentService.selectbyarticle(aid);
        return Result.success(commentslist);
    }

    @PutMapping("/audit")
    public Result audit(@RequestBody Comment comment) {
        commentService.audit(comment);
        return Result.success();
    }
/*
    @PutMapping("/unlock/{id}")
    public Result unlock(@PathVariable Integer id) {
        commentService.unlockComment(id);
        return Result.success();
    }

 */

}
