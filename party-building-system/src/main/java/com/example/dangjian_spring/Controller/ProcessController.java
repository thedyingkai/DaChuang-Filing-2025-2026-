package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Activity;
import com.example.dangjian_spring.entity.Process;
import com.example.dangjian_spring.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/process")
public class ProcessController {
    @Autowired
    private ProcessService processService;

    @PostMapping("/add")
    public Result add(@RequestBody Process process) {
        processService.add(process);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Process> processList = processService.selectAll();
        return Result.success(processList);
    }
/*
    @GetMapping("/selectBYtypeone")
    public Result selectBYtypeone() {
        List<Process> processList = processService.selectBytypeone();
        return Result.success(processList);
    }
 */
    @GetMapping("/selectBYptid/{id}")
    public Result selectByid(@PathVariable Integer id) {
        List<Process> processList = processService.selectByptid(id);
        return Result.success(processList);
    }


    @PutMapping("/updateAuditor")
    public Result updateAuditor(@RequestBody Process process) {
        processService.updateAuditor(process);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        processService.delete(id);
        return Result.success();
    }

}