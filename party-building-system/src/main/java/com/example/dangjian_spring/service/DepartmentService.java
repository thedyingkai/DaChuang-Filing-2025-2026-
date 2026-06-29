package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.DepartmentMapper;
import com.example.dangjian_spring.entity.Department;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.List;

@Service
@Transactional
public class DepartmentService extends  ServiceImpl<DepartmentMapper, Department>{
    @Resource
    private DepartmentMapper departmentMapper;

    public List<Department> selectAll(){
        return departmentMapper.selectAll();
    }

    private List<Department> selectDepartmentByseid(Integer seid){
        return departmentMapper.selectDepartmentByseid(seid);
    }

    public void add(Department department){
      departmentMapper.add(department);
    }

    public void delete(Integer id){
        departmentMapper.delete(id);
    }

    public void rename(Department department){ departmentMapper.rename(department);}


}
