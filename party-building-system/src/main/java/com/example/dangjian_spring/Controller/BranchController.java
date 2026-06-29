package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Branch;
import com.example.dangjian_spring.entity.Column;
import com.example.dangjian_spring.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/branch")
public class BranchController {
    @Autowired
    private BranchService branchService;

    @GetMapping("/selectAll")
    private Result selectAll(){
        List<Branch> branchList = branchService.selectAll();
        return Result.success(branchList);
    }

    @GetMapping("/selectByBid/{bid}")
    private Result selectByBid(@PathVariable Integer bid){
     Branch branch = branchService.selectByBid(bid);
     return Result.success(branch);
    }


    @PostMapping("/add")
    private Result add(@RequestBody Branch branch){
       branchService.add(branch);
       return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    private Result delete(@PathVariable Integer id){
        branchService.delete(id);
        return Result.success();
    }

    @PutMapping("/rename")
    private Result rename(@RequestBody Branch branch){
        branchService.update(branch);
        return Result.success();
    }
}
