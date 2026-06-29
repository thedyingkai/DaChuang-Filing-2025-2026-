package com.example.dangjian_spring.Controller;

import cn.hutool.core.date.DateUtil;
import com.example.dangjian_spring.common.Page;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Draft;
import com.example.dangjian_spring.entity.User;
import com.example.dangjian_spring.service.DraftService;
import com.example.dangjian_spring.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@ResponseBody
@RequestMapping("/draft")
public class DraftController {

    @Autowired
    DraftService draftService;


    /**
     * 插入草稿
     */
    @PostMapping("/add")
    public Result add(@RequestBody Draft draft) {
        User currentUser = TokenUtils.getCurrentUser1();
        draft.setUid(currentUser.getId());
        draft.setSave_time(DateUtil.now());
        draftService.insertDraft(draft);
        return Result.success();
    }

    /**
     * 修改草稿信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Draft draft) {
        draft.setSave_time(DateUtil.now());
        draftService.updateDraft(draft);
        return Result.success();
    }

    /**
     * 新增并提交草稿
     */
    @PostMapping("/new&submit")
    public Result newAndSubmit(@RequestBody Draft draft) {
        User currentUser = TokenUtils.getCurrentUser1();
        draft.setUid(currentUser.getId());
        draft.setSave_time(DateUtil.now());
        draft.setSend_time(DateUtil.now());
        draftService.newAndSubmit(draft);
        return Result.success();
    }

    /**
     * 提交草稿
     */
    @PutMapping("/submit")
    public Result submit(@RequestBody Draft draft) {
        draft.setSave_time(DateUtil.now());
        draft.setSend_time(DateUtil.now());
        draftService.submit(draft);
        return Result.success();
    }

    /**
     * 删除草稿信息
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        draftService.deleteDraft(id);
        return Result.success();
    }

    /**
     * 审查草稿时上锁
     */
    @PutMapping("/lock/{id}")
    public Result lock(@PathVariable Integer id) {
        draftService.lockDraft(id);
        return Result.success();
    }

    /**
     * 退出审查时解锁
     */
    @PutMapping("/unlock/{id}")
    public Result unlock(@PathVariable Integer id) {
        draftService.unlockDraft(id);
        return Result.success();
    }

    /**
     * 批量删除草稿信息
     */
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> dids) {
        draftService.batchDeleteDraft(dids);
        return Result.success();
    }

    /**
     * 查询指定草稿状态
     */
    @GetMapping("/checkStatus/{id}")
    public Result checkStatus(@PathVariable Integer id) {
        Integer currentStatus = draftService.checkStatus(id);
        return Result.success(currentStatus);
    }

    /**
     * 查询全部草稿信息
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Draft> draftList = draftService.selectAll();
        return Result.success(draftList);
    }

    /**
     * 查询全部待审草稿信息
     */
    @GetMapping("/selectAllToAudit")
    public Result selectAllToAudit() {
        List<Draft> draftList = draftService.selectAllToAudit();
        return Result.success(draftList);
    }

    /**
     * 查询指定uid草稿信息
     */
    @GetMapping("/selectByUid/{uid}")
    public Result selectByUid(@PathVariable Integer uid) {
        List<Draft> draftList = draftService.selectByUid(uid);
        return Result.success(draftList);
    }

    /**
     * 查询指定uid未提交草稿信息
     */
    @GetMapping("/selectLocalByUid/{uid}")
    public Result selectLocalByUid(@PathVariable Integer uid) {
        List<Draft> draftList = draftService.selectLocalByUid(uid);
        return Result.success(draftList);
    }

    /**
     * 查询指定uid已提交草稿信息
     */
    @GetMapping("/selectSubByUid/{uid}")
    public Result selectSubByUid(@PathVariable Integer uid) {
        List<Draft> draftList = draftService.selectSubByUid(uid);
        return Result.success(draftList);
    }

    /**
     * 查询指定uid已审核草稿信息
     */
    @GetMapping("/selectAuditedByUid/{uid}")
    public Result selectAuditedByUid(@PathVariable Integer uid) {
        List<Draft> draftList = draftService.selectAuditedByUid(uid);
        return Result.success(draftList);
    }

    /**
     * 查询指定did草稿信息
     */
    @GetMapping("/selectByDid/{did}")
    public Result selectByDid(@PathVariable Integer did) {
        Draft draft = draftService.selectByDid(did);
        return Result.success(draft);
    }

    /**
     * 查询指定dtitle草稿信息
     * 如果可能有多个结果，统一返回List对象集合
     */
    @GetMapping("/selectByTitle/{title}")
    public Result selectByTitle(@PathVariable String title) {
        Draft draft = draftService.selectByTitle(title);
        return Result.success(draft);
    }

    /**
     * 多条件查询草稿信息
     */
    @GetMapping("/selectByMore")
    public Result selectByMore(@RequestParam String title, @RequestParam Integer cid) {
        Draft draft = draftService.selectByMore(title, cid);
        return Result.success(draft);
    }

    /**
     * 多条件模糊查询草稿信息
     */
    @GetMapping("/selectByMoreLike")
    public Result selectByMoreLike(@RequestParam String title, @RequestParam Integer cid) {
        List<Draft> draftList = draftService.selectByMoreLike(title, cid);
        return Result.success(draftList);
    }

    /**
     * 多条件分页模糊查询草稿信息
     * PageNum 当前页码
     * PageSize 页面大小
     */
    @GetMapping("/selectByPage")
    public Result selectByMoreLikePage(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                                       @RequestParam String title, @RequestParam Integer cid) {
        Page<Draft> page = draftService.selectByMoreLikePage(pageNum, pageSize, title, cid);
        return Result.success(page);
    }
}
