package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Department;
import com.example.dangjian_spring.service.DepartmentService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody

@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/selectAll")
    public Result selectAll(){
        List<Department> departments = departmentService.selectAll();
        return Result.success(departments);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Department department) {
        departmentService.add(department);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable int id) {
        departmentService.delete(id);
        return Result.success();
    }

    @PutMapping("/rename")
    public Result rename(@RequestBody Department department) {
        departmentService.rename(department);
        return Result.success();
    }
}
