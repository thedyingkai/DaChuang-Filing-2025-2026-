package com.example.dangjian_spring.Controller;

import cn.hutool.core.date.DateUtil;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Audit;
import com.example.dangjian_spring.service.ArticleService;
import com.example.dangjian_spring.service.AuditService;
import com.example.dangjian_spring.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@ResponseBody
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    AuditService auditService;
    @Autowired
    DraftService draftService;
    @Autowired
    ArticleService articleService;
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
        draftService.updateStatusById(audit.getDid(),audit.getStatus());
        if (audit.getStatus()==2) {
         articleService.insertArticle(audit.getDid(), audit.getReply_time());

        }


        return Result.success();
    }
 */
   @PostMapping("/Update")
   public Result update(@RequestBody Audit audit) {
       audit.setTime(DateUtil.now());
        auditService.update(audit);
        return Result.success();
   }

    @GetMapping("/selectByUid/{uid}")
    public Result selectByUid(@PathVariable Integer uid) {
        List<Audit> auditList = auditService.selectByUid(uid);
        return Result.success(auditList);
    }

    /**
     * 查询编号为uid的审核员已审核草稿信息
     */
    @GetMapping("/selectAuditedByUid/{uid}")
    public Result selectAuditedByUid(@PathVariable Integer uid) {
        List<Audit> auditList = auditService.selectAuditedByUid(uid);
        return Result.success(auditList);
    }


    /**
     * 查询编号为id的提交记录相关审核信息
     */
    @GetMapping("/selectBySubmitId/{id}")
    public Result selectByDid(@PathVariable Integer id) {
        List<Audit> auditList = auditService.selectBySubmitId(id);
        return Result.success(auditList);
    }
}
