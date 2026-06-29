package com.example.dangjian_spring.Controller;

import cn.hutool.core.util.StrUtil;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Resource;
import com.example.dangjian_spring.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
@RequestMapping("/resource")
@Slf4j
public class ResourceController {
    @Autowired
    ResourceService resourceService;

    /**
     * 新增资源
     */
    @PostMapping("/add")
    public Result add(@RequestBody Resource resource) {
        if (resource.getUid() == null) {
            return Result.error("用户未登录或 uid 为空，请重新登录");
        }
        if (resource.getAcid() == null) {
            return Result.error("请选择要上传到的活动");
        }
        if (StrUtil.isBlank(resource.getName()) || StrUtil.isBlank(resource.getContent())) {
            return Result.error("资源名称或文件地址不能为空");
        }
        try {
            int rows = resourceService.insertResource(resource);
            if (rows < 1) {
                return Result.error("写入数据库失败（影响行数为 0），请确认存在 resource_view 表且活动 id 有效");
            }
            log.info("resource added id={} uid={} acid={} name={}", resource.getId(), resource.getUid(), resource.getAcid(), resource.getName());
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return Result.error("主键冲突，插入数据错误");
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("foreign key") || msg.contains("1452") || msg.contains("activity_to_resource")) {
                return Result.error("所选活动不存在或已被删除，请重新选择活动（可先执行 deploy/fix_resource_seed.sql）");
            }
            if (msg.contains("user_to_resource")) {
                return Result.error("当前用户无效，请退出后重新登录");
            }
            log.error("resource add SQL error uid={} acid={}", resource.getUid(), resource.getAcid(), e);
            return Result.error("SQL 插入数据错误: " + msg);
        } catch (Exception e) {
            log.error("resource add failed", e);
            return Result.error("系统错误: " + e.getMessage());
        }
        return Result.success();
    }

    /**
     * 查询全部资源信息
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Resource> resourceList = resourceService.selectAll();
        return Result.success(resourceList);
    }

    /**
     * 查询指定用户参加的活动相关资源信息
     */
    @GetMapping("/selectByJoiner")
    public Result selectByJoiner(@RequestParam String joiner) {
        List<Resource> resourceList = resourceService.selectByJoiner(joiner);
        return Result.success(resourceList);
    }

    /**
     * 查询指定uid,acid资源信息
     */
    @GetMapping("/selectByUidAndAcid")
    public Result selectByUidAndAcid(
            @RequestParam Integer uid,
            @RequestParam Integer acid) {
        List<Resource> resourceList = resourceService.selectByUidAndAcid(uid, acid);
        return Result.success(resourceList);
    }

    /**
     * 查询指定acid资源信息
     */
    @GetMapping("/selectByAcid/{acid}")
    public Result selectByAcid(@PathVariable Integer acid) {
        List<Resource> resourceList = resourceService.selectByAcid(acid);
        return Result.success(resourceList);
    }

    /**
     * 获取10张最近活动图片（按所属活动顺序排序）
     */
    @GetMapping("/selectLatestImages")
    public Result selectLatestImages() {
        List<Resource> resourceList = resourceService.selectLatestImages();
        return Result.success(resourceList);
    }

    /**
     * 统计资源信息
     */
    @GetMapping("/count")
    public Result count() {
        Map<String, Integer> resultMap = resourceService.countResourceTypes();
        return Result.success(resultMap);
    }

    /**
     * 统计各年月活动的资源信息
     */
    @GetMapping("/countEachMonth")
    public Result countEachMonth() {
        Map<String, Integer> resultMap = resourceService.countResourceEachMonth();
        return Result.success(resultMap);
    }

    @PutMapping("/batchMoveToActivity/{acid}")
    public Result moveSourceToColumn(@PathVariable Integer acid, @RequestBody List<Integer> selectedSourceIdList) {
        resourceService.batchMoveToActivity(acid, selectedSourceIdList);
        return Result.success();
    }
}
