package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Group;
import com.example.dangjian_spring.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @GetMapping("/selectByBid/{bid}")
    public Result selectByBid(@PathVariable Integer bid) {
        List<Group> groups = groupService.selectByBid(bid);
        return Result.success(groups);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Group group) {
     groupService.add(group);
     return Result.success();
    }

    @PutMapping("/rename")
    public Result rename(@RequestBody Group group) {
    groupService.update(group);
    return Result.success();
    }

    @PutMapping("/setleader")
    public Result setleader(@RequestBody Group group) {
        groupService.setleader(group);
        return Result.success();
    }

    @DeleteMapping("/delete/{gid}")
    public Result delete(@PathVariable Integer gid) {
        groupService.delete(gid);
        return Result.success();
    }
}
