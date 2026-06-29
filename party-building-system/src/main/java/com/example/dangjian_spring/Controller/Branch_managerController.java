package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Branch_manager;
import com.example.dangjian_spring.service.Branch_managerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/branch_manager")
public class Branch_managerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Branch_managerController.class);

    @Autowired
    private Branch_managerService branch_managerService;

    @GetMapping("/selectByBid/{bid}")
    public Result selectByBid(@PathVariable Integer bid) {
        List<Branch_manager> branch_managers = branch_managerService.selectByBid(bid);
        return Result.success(branch_managers);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Branch_manager branch_manager) {
        branch_managerService.add(branch_manager);
        return Result.success();
    }

    @PostMapping("/batchAdd")
    public Result batchAdd(@RequestBody List<Branch_manager> branch_managers) {
        LOGGER.debug("batchAdd branch_managers: {}", branch_managers);
        branch_managerService.batchAdd(branch_managers);
        return Result.success();
    }

    @PutMapping("/rename")
    public Result rename(@RequestBody Branch_manager branch_manager) {
        branch_managerService.rename(branch_manager);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        branch_managerService.delete(id);
        return Result.success();
    }

    @PutMapping("/changeManager")
    public Result changeManager(@RequestBody Branch_manager branch_manager) {
        int id = branch_manager.getId();
        int uid = branch_manager.getUid();
        branch_managerService.changeManager(id, uid);
        return Result.success();
    }
}
