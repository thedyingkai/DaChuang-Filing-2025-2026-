package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.CharaMapper;
import com.example.dangjian_spring.entity.Chara;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class CharaService extends ServiceImpl<CharaMapper, Chara> {

    @Resource
    private CharaMapper charaMapper;

    public int getCidByCname(String cname) {
        return charaMapper.getCidByCname(cname);
    }

    public void insertChara(Chara chara) {
        charaMapper.insert(chara);
    }

    public void updateChara(Chara chara) {
        charaMapper.updateChara(chara);
    }

    public void deleteChara(Integer cid) {
        charaMapper.deleteChara(cid);
    }

    public void batchDeleteChara(List<Integer> cids) {
        for (Integer cid : cids) {
            charaMapper.deleteChara(cid);
        }
    }

    public List<Chara> selectAll() {
        return charaMapper.selectAll();
    }

    public Chara selectByCid(Integer cid) {
        return charaMapper.selectByCid(cid);
    }

    public Chara selectByCname(String cname) {
        return charaMapper.selectByCname(cname);
    }

    // 获取所有身份和 cid 的映射关系
    public Map<String, Integer> getAllCnameCidMap() {
        List<Chara> charaList = charaMapper.selectAll();
        Map<String, Integer> cnameCidMap = new HashMap<>();
        for (Chara chara : charaList) {
            cnameCidMap.put(chara.getCname(), chara.getId());
        }
        return cnameCidMap;
    }
}