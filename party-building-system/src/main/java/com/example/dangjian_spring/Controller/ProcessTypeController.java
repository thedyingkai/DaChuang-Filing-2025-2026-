package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.ProcessType;
import com.example.dangjian_spring.service.ProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/processtype")
public class ProcessTypeController {
    @Autowired
    private ProcessTypeService processTypeService;

    @GetMapping("/selectAll")
    public Result selectAll(){
        List<ProcessType> processTypeList =processTypeService.selectall();
        return Result.success(processTypeList);
    }

    @GetMapping("/selectByBid/{bid}")
    public Result selectByBid(@PathVariable Integer bid) {
        List<ProcessType> processTypeList = processTypeService.selectByBid(bid);
        if (processTypeList == null || processTypeList.isEmpty()) {
            processTypeList = processTypeService.selectall();
        }
        return Result.success(processTypeList);
    }

    @PostMapping("/add")
    public Result add(@RequestBody ProcessType processType) {
        processTypeService.add(processType);
        return Result.success();
    }

    @PutMapping("/setType/{id}")
    public Result setType(@PathVariable Integer id) {
     processTypeService.setType(id);
     return Result.success();
    }

  @PutMapping("/setCoid")
  public Result setCoid(@RequestBody ProcessType processType) {
        processTypeService.setCoid(processType);
        return Result.success();
  }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        processTypeService.delete(id);
        return Result.success();
    }



}
