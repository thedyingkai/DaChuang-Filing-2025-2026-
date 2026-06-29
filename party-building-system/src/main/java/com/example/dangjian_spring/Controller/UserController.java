package com.example.dangjian_spring.Controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.dangjian_spring.common.Page;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.User;
import com.example.dangjian_spring.entity.User1ExportDTO;
import com.example.dangjian_spring.exception.ServiceException;
import com.example.dangjian_spring.service.CharaService;
import com.example.dangjian_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@ResponseBody
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    CharaService charaService;

    /**
     * 插入用户
     */
    @PostMapping("/add")
    public Result add(@RequestBody User User) {
        try {
            userService.insertUser(User);
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                return Result.error("插入数据错误");
            } else {
                return Result.error("系统错误");
            }
        }
        return Result.success();
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }

    /**
     * 删除用户信息
     */
    @DeleteMapping("/delete/{uid}")
    public Result delete(@PathVariable Integer uid) {
        userService.deleteUser(uid);
        return Result.success();
    }

    /**
     * 批量删除用户信息
     */
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> uids) {
        userService.batchDeleteUser(uids);
        return Result.success();
    }

    /**
     * 查询全部用户信息
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<User> userList = userService.selectAll();
        return Result.success(userList);
    }

    /**
     * 查询指定uid用户信息
     */
    @GetMapping("/selectByUid/{uid}")
    public Result selectByUid(@PathVariable Integer uid) {
        User user = userService.selectByUid(uid);
        return Result.success(user);
    }

    /**
     * 查询指定uname用户信息
     * 如果可能有多个结果，统一返回List对象集合
     */
    @GetMapping("/selectByUname/{uname}")
    public Result selectByUname(@PathVariable String uname) {
        User user = userService.selectByUname(uname);
        return Result.success(user);
    }

    /**
     * 多条件查询用户信息
     */
    @GetMapping("/selectByMore")
    public Result selectByMore(@RequestParam String uname, @RequestParam Integer cid) {
        User user = userService.selectByMore(uname, cid);
        return Result.success(user);
    }

    /**
     * 多条件模糊查询用户信息
     */
    @GetMapping("/selectByMoreLike")
    public Result selectByMoreLike(@RequestParam String uname, @RequestParam Integer cid) {
        List<User> userList = userService.selectByMoreLike(uname, cid);
        return Result.success(userList);
    }

    /**
     * 多条件分页模糊查询用户信息
     * PageNum 当前页码
     * PageSize 页面大小
     */
    @GetMapping("/selectByPage")
    public Result selectByMoreLikePage(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                                       @RequestParam String uname, @RequestParam Integer cid) {
        Page<User> page = userService.selectByMoreLikePage(pageNum, pageSize, uname, cid);
        return Result.success(page);
    }

    /**
     * 批量导出
     */
    @GetMapping("/export")
    public void exportData(@RequestParam(required = false) String name, HttpServletResponse response) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        List<User1ExportDTO> list = new ArrayList<>();
        int index = 1; // 用于生成连续编号的计数器
        if (StringUtils.isBlank(name)) {
            List<User> userList = userService.selectAllToExport();
            // 将 User 对象转换为 User1ExportDTO 对象
            for (User user : userList) {
                User1ExportDTO exportDTO = User1ExportDTO.builder()
                        .uid(index++) // 使用计数器生成连续编号
                        .uname(user.getUname())
                        .psw(user.getPsw())
                        .cname(user.getCname())
                        .build();
                list.add(exportDTO);
            }
        }
        writer.addHeaderAlias("uid", "编号");
        writer.addHeaderAlias("uname", "用户名");
        writer.addHeaderAlias("psw", "密码");
        writer.addHeaderAlias("cname", "身份");
        writer.write(list, true);
        response.setContentType("application/vnd.openxmlformats.officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("用户信息表", "UTF-8") + ".xlsx");
        //获取输出流
        ServletOutputStream outputStream = response.getOutputStream();
        //将 excel 写入到输出流里，并设置用完流之后就关闭
        writer.flush(outputStream, true);
    }

    /**
     * 批量导入
     */
    @PostMapping("/import")
    public Result importData(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Integer> cnameCidMap = new HashMap<>();
        try {
            // 先获取身份和 cid 的映射关系
            cnameCidMap = charaService.getAllCnameCidMap();
        } catch (Exception e) {
            throw new ServiceException("获取身份和 cid 映射关系失败");
        }
        try {
            // 获取 inputStream 流
            InputStream inputStream = file.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            // 添加表头别名映射
            reader.addHeaderAlias("用户名", "uname");
            reader.addHeaderAlias("密码", "psw");
            reader.addHeaderAlias("身份", "cname");
            List<User> userList = reader.readAll(User.class);
            // 根据身份信息设置 cid
            for (User user : userList) {
                String cname = user.getCname();
                // 从映射关系中查找 cid
                int cid = cnameCidMap.getOrDefault(cname, -1);
                user.setCid(cid);
            }
            userService.saveBatch(userList);
        } catch (Exception e) {
            throw new ServiceException("文件上传失败");
        }
        return Result.success();
    }

    @GetMapping("/selectByDeid/{deid}")
    public Result selectByDeid(@PathVariable Integer deid) {
        List<User> userList = userService.selectByDeid(deid);
        return Result.success(userList);
    }

    @GetMapping("/selectBranchByGid/{gid}")
    public Result selectBranchByGid(@PathVariable Integer gid) {
        List<User> userList = userService.selectBranchByGid(gid);
        return Result.success(userList);
    }

    @GetMapping("/selectByGid/{gid}")
    public Result selectByGid(@PathVariable Integer gid) {
        List<User> userList = userService.selectByGid(gid);
        return Result.success(userList);
    }

    @PutMapping("/updateDeid")
    public Result updateDeid(@RequestBody User user) {
        userService.updateDeid(user);
        return Result.success();
    }

    @PutMapping("/updateAvatar")
    public Result updateAvatar(@RequestBody User user) {
        userService.updateAvatar(user);
        return Result.success();
    }

    @PutMapping("/updateUsername")
    public Result updateUsername(@RequestBody User user) {
        userService.updateUsername(user);
        return Result.success();
    }

    @PutMapping("/updateAccount")
    public Result updateAccount(@RequestBody User user) {
        userService.updateAccount(user);
        return Result.success();
    }

    @GetMapping("/selectByBranch/{bid}")
    public Result selectByBranch(@PathVariable Integer bid) {
        List<User> userList = userService.selectByBranch(bid);
        return Result.success(userList);
    }

    @GetMapping("/selectAuditor")
    public Result selectAuditor() {
        List<User> userList = userService.selectAuditor();
        return Result.success(userList);
    }

    @PutMapping("/deletegid/{id}")
    public Result deletegid(@PathVariable Integer id) {
        userService.deleteGid(id);
        return Result.success();
    }

    @PutMapping("/setgid")
    public Result setgid(@RequestBody List<Integer> ids,@RequestParam Integer gid) {
        userService.updateGids(ids,gid);
        return Result.success();
    }

}
