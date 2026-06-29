package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.ArticleMapper;
import com.example.dangjian_spring.dao.mapper.DraftMapper;
import com.example.dangjian_spring.dao.mapper.SubmitMapper;
import com.example.dangjian_spring.entity.Article;
import com.example.dangjian_spring.entity.Draft;
import com.example.dangjian_spring.entity.Submit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;

@Service
@Transactional
public class ArticleService extends ServiceImpl<ArticleMapper, Article> {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private DraftMapper draftMapper;
    @Resource
    private SubmitMapper submitMapper;

    /**
     * 按草稿主键从 {@code draft_view} 发稿（历史接口；新流程请用 {@link #publishFromSubmit}）。
     */
    public void insertArticle(Integer draftId, String time) {
        Draft draft = draftMapper.selectDraftByPk(draftId);
        if (draft == null) {
            throw new IllegalStateException("draft not found: " + draftId);
        }
        Article article = new Article();
        article.setCoid(draft.getCoid());
        article.setUid(draft.getUid());
        article.setContent(draft.getContent());
        article.setTitle(draft.getTitle());
        article.setPublish_time(time);
        article.setSource(draft.getSource());
        article.setShow(0);
        articleMapper.insert(article);
    }

    /**
     * 审核通过后发稿：正文以 {@code submit_view} 为准（与审核编辑保存一致），栏目与作者在草稿上取。
     */
    public void publishFromSubmit(Integer submitId, String publishTime) {
        Submit submit = submitMapper.selectById(submitId);
        if (submit == null || submit.getDid() == null) {
            throw new IllegalStateException("submit not found: " + submitId);
        }
        Draft draft = draftMapper.selectDraftByPk(submit.getDid());
        if (draft == null) {
            throw new IllegalStateException("draft not found: " + submit.getDid());
        }
        Article article = new Article();
        article.setCoid(draft.getCoid());
        article.setUid(draft.getUid());
        article.setContent(submit.getContent() != null ? submit.getContent() : draft.getContent());
        article.setTitle(submit.getTitle() != null ? submit.getTitle() : draft.getTitle());
        article.setSource(submit.getSource() != null ? submit.getSource() : draft.getSource());
        article.setPublish_time(publishTime);
        article.setShow(0);
        articleMapper.insert(article);
    }

    public List<Article> selectAll() {
        return articleMapper.selectAll();
    }

    public List<Article> selectByCoid(Integer id) {
        return articleMapper.selectByCoid(id);
    }


    public Article selectByid(Integer id) {
        return articleMapper.selectByid(id);
    }


    public void moveToColumn(Integer coid, Integer id) {
        articleMapper.updateCoid(coid, id);
    }

    public void batchMoveToColumn(Integer coid, List<Integer> articleIdList) {
        if (articleIdList == null || articleIdList.isEmpty()) {
            return;
        }
        articleMapper.batchUpdateCoid(articleIdList, coid);
    }

    public List<Article> selectByUid(Integer uid) {return articleMapper.selectByUid(uid);}

    public List<Article> selectByBranch() {return articleMapper.selectByBranch();}
}
