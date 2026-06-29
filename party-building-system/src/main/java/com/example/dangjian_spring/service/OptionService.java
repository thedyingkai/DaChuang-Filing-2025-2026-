package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dangjian_spring.dao.mapper.OptionMapper;
import com.example.dangjian_spring.entity.MultipleChoiceOption;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class OptionService extends ServiceImpl<OptionMapper, MultipleChoiceOption>{
    @Resource
    private OptionMapper optionMapper;

    public List<MultipleChoiceOption> allOptionInQuestion(int id)
    {
        return optionMapper.allOptionsInQuestion(id);
    }

    public List<MultipleChoiceOption> allOptions()
    {
        return optionMapper.allOptions();
    }

    public MultipleChoiceOption selectOptionById(int id)
    {
        return optionMapper.selectOptionById(id);
    }

    public int updateOption(MultipleChoiceOption option)
    {
        return optionMapper.updateOption(option);
    }

    public int addOption(MultipleChoiceOption option)
    {
        return optionMapper.addOption(option);
    }

    public int deleteOption(int id)
    {
        return optionMapper.deleteOption(id);
    }
}
