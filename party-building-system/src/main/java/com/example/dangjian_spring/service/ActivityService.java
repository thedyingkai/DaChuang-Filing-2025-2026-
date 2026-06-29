package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.ActivityMapper;
import com.example.dangjian_spring.dao.mapper.ActivityParticipationMapper;
import com.example.dangjian_spring.entity.Activity;
import com.example.dangjian_spring.entity.ActivityParticipation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.Resource;

import java.util.List;

@Service
@Transactional
@Slf4j
public class ActivityService extends ServiceImpl<ActivityMapper, Activity> {
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityParticipationMapper activityParticipationMapper;

    public void add(Activity activity) {
        this.activityMapper.add(activity);
        Integer acid = activity.getId();
        for (ActivityParticipation user : activity.getMembers()) {
            if (user.getType() == null) {
                user.setType(0);
            }
            user.setAcid(acid);
            log.debug("activity participation: {}", user);
            this.activityParticipationMapper.insert(user);
        }
    }

    public void update(Activity activity) {
        this.activityMapper.update(activity);
        this.activityMapper.clearParticipation(activity.getId());
        Integer acid = activity.getId();
        for (ActivityParticipation user : activity.getMembers()) {
            this.activityMapper.participate(acid, user.getId());
        }
    }

    public void delete(int id) {
        this.activityMapper.delete(id);
    }

    public List<Activity> selectAll() {
        return this.activityMapper.selectAll();
    }

    public void rename(Activity activity) {
        this.activityMapper.rename(activity);
    }

    public List<Activity> selectAllExceptDefault() {
        return this.activityMapper.selectAllExceptDefault();
    }

    public List<Activity> selectByTypeId(Integer typeId, Integer uid) {
        return this.activityMapper.selectByTypeId(typeId, uid);
    }

    public Activity selectById(Integer id) {
        return this.activityMapper.selectById(id);
    }

    public void updateCoverImage(Activity activity) {
        this.activityMapper.updateCoverImage(activity);
    }
}
