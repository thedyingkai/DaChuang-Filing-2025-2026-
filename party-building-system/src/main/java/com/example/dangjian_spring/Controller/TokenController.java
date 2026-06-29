package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.entity.User2;
import com.example.dangjian_spring.utils.TokenUtils;


import com.example.dangjian_spring.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    @PostMapping("/token/create")
    public Result createToken(@RequestBody User2 user2)
    {
        String u_id = user2.getUser_id().toString();
        String pw = user2.getPassword();

        return Result.success(TokenUtils.createToken2(u_id,pw));
    }

    @GetMapping("/token/get_user")
    public Result useToken()
    {
        return Result.success(TokenUtils.getCurrentUser2());
    }
}
