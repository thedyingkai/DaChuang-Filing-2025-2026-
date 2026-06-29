package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.HomePicMapper;
import com.example.dangjian_spring.entity.HomePic;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;
import java.util.List;
@Service
@Slf4j
public class HomePicService extends ServiceImpl<HomePicMapper, HomePic>{
    @Resource
    private HomePicMapper homePicMapper;

    public List<HomePic> listAllHomePics()
    {
        return homePicMapper.select_all_homePic();
    }

    public int addHomePic(HomePic homePic)
    {
        return homePicMapper.add_homePic(homePic);
    }


    public void deleteAllHomePics()
    {
        try {
            homePicMapper.delete_all_homePics();
        }catch (Exception e){
            log.warn("deleteAllHomePics failed", e);
        }
    }

}
