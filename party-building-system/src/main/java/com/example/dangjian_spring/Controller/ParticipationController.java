package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.dao.mapper.User1Mapper;
import com.example.dangjian_spring.entity.Participation;
import com.example.dangjian_spring.service.ParticipationService;
import com.example.dangjian_spring.service.PaperService;
import com.example.dangjian_spring.service.User2Service;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
public class ParticipationController {
    @Resource
    ParticipationService participationService;
    @Resource
    PaperService paperService;
    @Resource
    User2Service user2Service;
    @Resource
    User1Mapper user1Mapper;

    @GetMapping("/participation")
    public Result allParticipation()
    {
        return Result.success(participationService.allParticipation());
    }

    @GetMapping("/participation/paper/{id}")
    public Result allParticipationByPaper(@PathVariable int id)
    {
        return Result.success(participationService.allParticipationByPaper(id));
    }
    @GetMapping("/participation/paperwithname/{id}")
    public Result allParticipationAndNameByPaper(@PathVariable int id)
    {
        return Result.success(participationService.allParticipationAndNameByPaper(id));
    }

    @GetMapping("/participation/user/{id}")
    public Result allParticipationByUser(@PathVariable int id)
    {
        return Result.success(participationService.allParticipationByUser(id));
    }

    @PostMapping("/participation/add")
    public Result addParticipation(@RequestBody Participation participation)
    {
        if (!userExistsForParticipation(participation.getUser_id())) {
            return Result.error("用户" + participation.getUser_id() + "不存在");
        }
        if (paperService.selectPaperById(participation.getTest_paper_id()) == null) {
            return Result.error("试卷" + participation.getTest_paper_id() + "不存在");
        }
        if (participationService.allParticipationByPaperAndUser(participation.getTest_paper_id(), participation.getUser_id()).isEmpty()) {
            if (participationService.addParticipation(participation) != 0) {
                return Result.success("添加成功");
            }
            return Result.error("添加失败");
        }
        if (participationService.updateParticipation(participation) != 0) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @PostMapping("/participation/update")
    public Result updateParticipation(@RequestBody Participation participation)
    {
        if (!userExistsForParticipation(participation.getUser_id())) {
            return Result.error("用户" + participation.getUser_id() + "不存在");
        }
        if (paperService.selectPaperById(participation.getTest_paper_id()) == null) {
            return Result.error("试卷" + participation.getTest_paper_id() + "不存在");
        }
        if (participationService.updateParticipation(participation) != 0) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /** participation_o 外键指向 user1；答题账号也可能在 User2，任一存在即可 */
    private boolean userExistsForParticipation(int userId) {
        return user2Service.getUserById(userId) != null || user1Mapper.selectByUid(userId) != null;
    }

}
