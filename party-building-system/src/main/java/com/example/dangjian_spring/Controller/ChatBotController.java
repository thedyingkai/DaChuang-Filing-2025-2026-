package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.service.LangchainChatchatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chatBot")
public class ChatBotController {

    @Autowired
    private LangchainChatchatService langchainChatchatService;

    /**
     * 流式输出
     */
    @GetMapping("/chat")
    public Flux<String> streamChat(@RequestParam String msg) {
        return langchainChatchatService.callChatApi(msg);
    }

}