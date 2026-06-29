package com.example.dangjian_spring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LangchainChatchatService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LangchainChatchatService() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30));
        this.webClient = WebClient.builder()
                .baseUrl("http://127.0.0.1:7861")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public Flux<String> callChatApi(String msg) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", List.of(Map.of(
                "content", msg,
                "role", "user",
                "name", "string"
        )));
        requestBody.put("model", "deepseek-r1");
        requestBody.put("stream", true);

        return webClient.post()
                .uri("/chat/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::extractContentFromResponse);
    }

    private String extractContentFromResponse(String responseLine) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseLine);
            JsonNode choices = jsonNode.path("choices");
            if (!choices.isMissingNode() && choices.isArray() && choices.size() > 0) {
                JsonNode delta = choices.get(0).path("delta");
                if (!delta.isMissingNode()) {
                    JsonNode content = delta.path("content");
                    if (!content.isMissingNode()) {
                        return content.asText();
                    }
                }
            }
        } catch (Exception e) {
            // 处理解析异常，这里简单忽略非 JSON 行
        }
        return "";
    }
}