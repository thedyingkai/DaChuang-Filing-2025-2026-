package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Department;
import com.example.dangjian_spring.entity.Sector;
import com.example.dangjian_spring.service.DepartmentService;
import com.example.dangjian_spring.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/sector")
public class SectorController {
    @Autowired
    private SectorService sectorService;

    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Sector> sectorList=sectorService.selectall();
        return Result.success(sectorList);
    };

    @PostMapping("/add")
    public Result add(@RequestBody Sector sector) {
        sectorService.add(sector);
        return Result.success();
    };

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable int id) {
        sectorService.delete(id);
        return Result.success();
    }

    @PutMapping("/rename")
    public Result rename(@RequestBody Sector sector) {
        sectorService.rename(sector);
        return Result.success();
    }

}
