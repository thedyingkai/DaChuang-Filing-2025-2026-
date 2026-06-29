package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.*;
import com.example.dangjian_spring.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class HomePicController {
    @Resource
    HomePicService homePicService;

    @GetMapping("/homePic/all")
    public Result listAllHomePics()
    {
        return Result.success(homePicService.listAllHomePics());
    }

    @PostMapping("/homePic/update")
    public Result updateHomePics(@RequestBody String jsonData)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<HomePic> homePics = objectMapper.readValue(jsonData, new TypeReference<List<HomePic>>() {});
            log.debug("Deserialized homePics count: {}", homePics != null ? homePics.size() : 0);
            homePicService.deleteAllHomePics();
            int id = 1;
            StringBuilder msg = new StringBuilder();
            for(HomePic homePic:homePics)
            {
                homePic.setId(id);
                if(homePicService.addHomePic(homePic) != 0)
                {
                    id = id + 1;
                }
                else
                {
                    msg.append("添加").append(homePic.getSource()).append("失败\n");
                }
            }
            if (!msg.isEmpty()) {
                return Result.success(msg.toString().trim());
            }
            return Result.success("轮播图更新完毕");
        } catch (Exception e) {
            log.error("homePic update JSON error", e);
            return Result.error("Invalid JSON data");
        }

    }
}