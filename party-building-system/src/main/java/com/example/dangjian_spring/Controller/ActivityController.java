package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Activity;
import com.example.dangjian_spring.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @PostMapping("/add")
    public Result add(@RequestBody Activity activity) {
        activityService.add(activity);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Activity activity) {
        activityService.update(activity);
        return Result.success();
    }

    @PutMapping("/updateCoverImage")
    public Result updateCoverImage(@RequestBody Activity activity) {
        activityService.updateCoverImage(activity);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Activity> activityList = activityService.selectAll();
        return Result.success(activityList);
    }

    @GetMapping("/selectAllExceptDefault")
    public Result selectAllExceptDefault() {
        List<Activity> activityList = activityService.selectAllExceptDefault();
        return Result.success(activityList);
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        activityService.delete(id);
        return Result.success();
    }

    @PutMapping("/rename")
    public Result rename(@RequestBody Activity activity) {
        activityService.rename(activity);
        return Result.success();
    }

    @GetMapping("/selectByTypeId")
    public Result selectByTypeId(@RequestParam Integer typeId,@RequestParam Integer uid) {
        List<Activity> activityList = activityService.selectByTypeId(typeId,uid);
        return Result.success(activityList);
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Activity activity = activityService.selectById(id);
        return Result.success(activity);
    }
}
