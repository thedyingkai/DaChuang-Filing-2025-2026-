package com.example.dangjian_spring.Controller;

import cn.hutool.core.date.DateUtil;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Column;
import com.example.dangjian_spring.service.ColumnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@ResponseBody
@RequestMapping("/column")
@Slf4j
public class ColumnController {

    @Autowired
    ColumnService columnService;

    /**
     * 新增栏目
     */
    @PostMapping("/add")
    public Result add(@RequestBody Column column) {
        columnService.insertColumn(column);
        return Result.success(column);
    }

    /**
     * 查询全部栏目信息
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Column> columnList = columnService.buildTree();
        return Result.success(columnList);
    }

    @AuthAccess
    @GetMapping("/selectAllToShow")
    public Result selectAllToShow() {
        List<Column> columnList = columnService.buildTreeToShow();
        return Result.success(columnList);
    }

    @AuthAccess
    @GetMapping("/selectByFather/{coid}")
    public Result selectByFather(@PathVariable Integer coid) {
        List<Column> columnList = columnService.selectByFather(coid);
        return Result.success(columnList);
    }

    /**
     * 编辑栏目名称
     */
    @PutMapping("/edit")
    public Result updateNameById(@RequestBody Column column) {
        log.debug("column edit: {}", column);
        columnService.updateNameById(column);
        return Result.success();
    }

    /**
     * 修改受影响栏目index
     */
    @PutMapping("/updateIndex/{increment}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result updateIndex(@RequestBody List<Column> columnList, @PathVariable Integer increment) {
        if (columnList != null && !columnList.isEmpty()) {
            columnService.updateIndexById(columnList, increment);
        }
        return Result.success();
    }

    /**
     * 修改栏目
     */
    @PutMapping("/update")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result updateById(@RequestBody Column column) {
        columnService.updateColumnById(column);
        return Result.success();
    }

    /**
     * 删除栏目
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        columnService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/getlist/{id}")
    public Result getList(@PathVariable Integer id) {
        List<Column> columnList = columnService.selectcolumnlist(id);
        return Result.success(columnList);
    }

}
