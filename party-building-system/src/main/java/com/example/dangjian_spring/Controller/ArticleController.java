package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Audit;
import com.example.dangjian_spring.entity.Article;
import com.example.dangjian_spring.service.ArticleService;
import com.example.dangjian_spring.service.AuditService;
import com.example.dangjian_spring.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@ResponseBody
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    AuditService auditService;
    @Autowired
    DraftService draftService;
    @Autowired
    private ArticleService articleService;

    /**
     * 新增审核记录
     */
      /*
    @PostMapping("/add")
    public Result add(@RequestBody Audit audit) {

        User currentUser = TokenUtils.getCurrentUser1();
        audit.setUid(currentUser.getId());
        audit.setSend_time(draftService.selectSendTimeById(audit.getDid()));
        audit.setReply_time(DateUtil.now());
        auditService.insertAudit(audit);
        draftService.updateStatusById(audit.getDid(), audit.getStatus());

        return Result.success();
    }
     */
    @GetMapping("/selectAllArticle")
    public Result selectAllArticle() {
        List<Article> articleList = articleService.selectAll();
        return Result.success(articleList);
    }

    @AuthAccess
    @GetMapping("/selectArticleBycoid/{coid}")
    public Result selectArticleBycoid(@PathVariable Integer coid) {
        List<Article> articleList = articleService.selectByCoid(coid);
        return Result.success(articleList);
    }

    @AuthAccess
    @GetMapping("/selectArticleByuid/{uid}")
    public Result selectArticleByuid(@PathVariable Integer uid) {
        List<Article> articleList = articleService.selectByUid(uid);
        return Result.success(articleList);
    }

    @AuthAccess
    @GetMapping("/selectArticleBybranch")
    public Result selectArticleBybranch() {
        List<Article> articleList = articleService.selectByBranch();
        return Result.success(articleList);
    }

    @AuthAccess
    @GetMapping("/selectArticleByid/{id}")
    public Result selectArticleByid(@PathVariable Integer id) {
        Article article = articleService.selectByid(id);
        return Result.success(article);
    }

    @PutMapping("/moveToColumn/{coid}")
    public Result moveArticleToColumn(@PathVariable Integer coid, @RequestBody Article article) {
        articleService.moveToColumn(coid,article.getId());
        return Result.success();
    }

    @PutMapping("/batchMoveToColumn/{coid}")
    public Result moveArticleToColumn(@PathVariable Integer coid, @RequestBody List<Integer> articleIdList) {
        articleService.batchMoveToColumn(coid,articleIdList);
        return Result.success();
    }
}
