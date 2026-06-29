package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.DepartmentMapper;
import com.example.dangjian_spring.dao.mapper.SectorMapper;
import com.example.dangjian_spring.entity.Column;
import com.example.dangjian_spring.entity.Department;
import com.example.dangjian_spring.entity.Sector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SectorService extends ServiceImpl<SectorMapper, Sector> {
    @Resource
    private SectorMapper sectorMapper;
    @Resource
    private DepartmentMapper departmentMapper;

    public void add(Sector sector) { sectorMapper.add(sector);}
    public void delete(Integer id){sectorMapper.delete(id);}
    public void rename(Sector sector){sectorMapper.rename(sector);}

    public List<Sector> selectall() {
        List<Sector> sectorList = sectorMapper.selectall();
        return buildtree(sectorList);
    }

    public List<Sector> buildtree(List<Sector> sectorList) {
        List<Department> result = new ArrayList<>();
        for (Sector sector : sectorList) {
            result = departmentMapper.selectDepartmentByseid(sector.getId());
            sector.setChildren(result);
        }
        return sectorList;
    }

    ;

}
