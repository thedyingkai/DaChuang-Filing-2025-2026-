package com.example.dangjian_spring.Controller;

import cn.hutool.core.util.StrUtil;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Column2;
import com.example.dangjian_spring.service.Column2Service;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
public class Column2Controller {
    @Resource
    Column2Service column2Service;

    @GetMapping("/columns")
    public Result allColumns()
    {
        return Result.success(column2Service.allColumns());
    }

    @GetMapping("/columns/delete/{id}")
    public Result deleteColumn(@PathVariable int id)
    {
        if(column2Service.selectColumnById(id) == null)
        {
            return Result.error("栏目" + id + "不存在");
        }
        else
        {
            if(column2Service.deleteColumn(id) != 0)
            {
                return Result.success("删除成功");
            }
            else
            {
                return Result.error("删除失败");
            }
        }
    }

    @GetMapping("/columns/search/{keyword}")
    public Result searchColumns(@PathVariable String keyword)
    {
        List<Column2> columns = column2Service.searchColumnsByDes(keyword);
        return Result.success(columns);
    }

    @PostMapping("/columns/add")
    public Result addColumn(@RequestBody Column2 column)
    {
        if(column2Service.selectColumnByDes(column.getColumn_description()) != null)
        {
            return Result.error("栏目" + column.getColumn_description() + "已存在");
        }
        else
        {
            int id = column2Service.allColumns().size() + 1;
            column.setColumn_id(id);
            if(column2Service.addColumn(column) != 0)
            {
                return Result.success(column2Service.allColumns());
            }
            else
            {
                return Result.error("添加失败");
            }
        }
    }

    @PostMapping("/columns/update")
    public Result updateColumn(@RequestBody Column2 column)
    {
        if(column2Service.selectColumnById(column.getColumn_id()) == null)
        {
            return Result.error("栏目" + column.getColumn_id() + "不存在");
        }
        else
        {
            if(column2Service.updateColumn(column) != 0)
            {
                return Result.success(column2Service.allColumns());
            }
            else
            {
                return Result.error("更新失败");
            }
        }
    }
}
