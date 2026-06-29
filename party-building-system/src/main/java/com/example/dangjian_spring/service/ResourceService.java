package com.example.dangjian_spring.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.ResourceMapper;
import com.example.dangjian_spring.entity.Resource;
import com.example.dangjian_spring.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ResourceService extends ServiceImpl<ResourceMapper, Resource> {
    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    public List<Resource> selectByUidAndAcid(Integer uid, Integer acid) {
        return resourceMapper.selectByUidAndAcid(uid, acid);
    }

    public int insertResource(Resource resource) {
        if (resourceMapper.countUserByUid(resource.getUid()) < 1) {
            throw new ServiceException("用户 id=" + resource.getUid() + " 在 user1 中不存在，请重新登录");
        }
        if (resourceMapper.countActivityByAcid(resource.getAcid()) < 1) {
            throw new ServiceException("活动 id=" + resource.getAcid()
                    + " 不存在。请先在「活动管理」创建活动，或在库中执行 deploy/fix_resource_seed.sql 插入未分类活动(acid=-1)");
        }
        // re_savetime 为 DATE 类型，勿写入带时分秒的字符串
        resource.setSavetime(DateUtil.today());
        return resourceMapper.insert(resource);
    }

    public List<Resource> selectByAcid(Integer acid) {
        return resourceMapper.selectByAcid(acid);
    }

    public void batchMoveToActivity(Integer acid, List<Integer> selectedSourceIdList) {
        resourceMapper.batchUpdateAcid(selectedSourceIdList, acid);
    }

    public List<Resource> selectLatestImages() {
        return resourceMapper.selectLatestImages();
    }

    public Map<String, Integer> countResourceTypes() {
        int imageAmount = 0;
        int videoAmount = 0;
        int audioAmount = 0;
        int fileAmount = 0;

        List<Map<String, Object>> results = resourceMapper.countResourceTypes();
        for (Map<String, Object> result : results) {
            int type = ((Number) result.get("type")).intValue();
            int resourceCount = ((Number) result.get("resource_count")).intValue();

            switch (type) {
                case 1:
                    imageAmount = resourceCount;
                    break;
                case 2:
                    videoAmount = resourceCount;
                    break;
                case 3:
                    audioAmount = resourceCount;
                    break;
                default:
                    fileAmount += resourceCount;
                    break;
            }
        }
        Map<String, Integer> resultMap = new HashMap<>();
        resultMap.put("imageAmount", imageAmount);
        resultMap.put("videoAmount", videoAmount);
        resultMap.put("audioAmount", audioAmount);
        resultMap.put("fileAmount", fileAmount);
        return resultMap;
    }

    public Map<String, Integer> countResourceEachMonth() {
        List<Map<String, Object>> results = resourceMapper.countResourceEachMonth();
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String, Object> result : results) {
            String yearMonth = (String) result.get("ym");
            if (yearMonth == null) {
                yearMonth = (String) result.get("year_month");
            }
            if (yearMonth != null) {
                Integer resourceCount = ((Number) result.get("resource_count")).intValue();
                resultMap.put(yearMonth, resourceCount);
            }
        }
        return resultMap;
    }

    public List<Resource> selectAll() {
        return resourceMapper.selectAll();
    }

    public List<Resource> selectByJoiner(String joiner) {
        return resourceMapper.selectByJoiner(joiner);
    }
}
