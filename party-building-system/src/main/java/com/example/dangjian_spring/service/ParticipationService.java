package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.ParticipationMapper;
import com.example.dangjian_spring.entity.Participation;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class ParticipationService extends ServiceImpl<ParticipationMapper, Participation> {
    @Resource
    private ParticipationMapper participationMapper;

    public List<Participation> allParticipation()
    {
        return participationMapper.allParticipation();
    }

    public List<Participation> allParticipationByUser(int id)
    {
        return participationMapper.allParticipationByUser(id);
    }

    public List<Participation> allParticipationByPaper(int id)
    {
        return participationMapper.allParticipationByPaper(id);
    }
    public List<Participation> allParticipationAndNameByPaper(int id)
    {
        return participationMapper.allParticipationAndNameByPaper(id);
    }
    public List<Participation> allParticipationByPaperAndUser(int pid,int uid)
    {
        return participationMapper.allParticipationByPaperAndUser(pid,uid);
    }
    public int addParticipation(Participation participation)
    {
        return participationMapper.addParticipation(participation);
    }

    public int updateParticipation(Participation participation)
    {
        return participationMapper.updateParticipation(participation);
    }
}
