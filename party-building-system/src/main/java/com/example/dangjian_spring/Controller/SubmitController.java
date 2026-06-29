package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Draft;
import com.example.dangjian_spring.entity.Process;
import com.example.dangjian_spring.entity.Submit;
import com.example.dangjian_spring.exception.ServiceException;
import com.example.dangjian_spring.service.AuditService;
import com.example.dangjian_spring.service.ProcessService;
import com.example.dangjian_spring.service.SubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/submit")
@Slf4j
public class SubmitController {
    @Autowired
    private SubmitService submitService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private AuditService auditService;

    /**
     * 由撰稿流程（如 {@link com.example.dangjian_spring.service.DraftService}）或 HTTP 客户端提交。
     * 必须使用 JSON 请求体；{@code @RequestParam} 无法绑定复杂对象。
     */
    @PostMapping("/add")
    public Result add(@RequestBody Draft draft) {
        Submit submit = new Submit();
        submit.setDid(draft.getId());
        submit.setContent(draft.getContent());
        submit.setTitle(draft.getTitle());
        submit.setSource(draft.getSource());
        List<Process> processList = processService.selectByCoid(draft.getCoid(), draft.getBid());
        log.debug("submit add processes: {}", processList);
        if (processList == null || processList.isEmpty()) {
            throw new ServiceException("400", "该栏目未配置审核流程或未绑定审核人，无法创建提交记录。");
        }
        submit.setId(submitService.add(submit));
        auditService.addAuditList(processList, submit);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Submit submit) {
        submitService.update(submit);
        return Result.success();
    }
}
