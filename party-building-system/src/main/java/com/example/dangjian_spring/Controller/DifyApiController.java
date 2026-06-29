package com.example.dangjian_spring.Controller;

import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.Article;
import com.example.dangjian_spring.entity.User;
import com.example.dangjian_spring.exception.ServiceException;
import com.example.dangjian_spring.service.DifyApiService;
import com.example.dangjian_spring.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Dify工作流集成的HTTP API端点
 * 所有接口默认需要登录Token（与现有 /api/* 一致，由SecurityConfig保护）
 * 写操作额外要求管理员身份
 */
@RestController
@RequestMapping("/api/dify")
@Slf4j
public class DifyApiController {

    @Resource
    private DifyApiService difyApiService;

    @Resource
    private UserService userService;

    // ==================== 文章 ====================

    @GetMapping("/articles/search")
    public Result searchArticles(@RequestParam String keyword) {
        return difyApiService.searchArticlesByTitle(keyword);
    }

    @GetMapping("/articles")
    public Result getArticles(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return difyApiService.getArticles(pageNum, pageSize);
    }

    @GetMapping("/articles/{id}")
    public Result getArticleById(@PathVariable Integer id) {
        return difyApiService.getArticleById(id);
    }

    @PostMapping("/articles")
    public Result createArticle(@RequestBody Article article) {
        return difyApiService.createArticle(article);
    }

    @PutMapping("/articles/{id}")
    public Result updateArticle(@PathVariable Integer id, @RequestBody Article article) {
        return difyApiService.updateArticle(id, article);
    }

    // ==================== 用户 ====================

    @GetMapping("/users")
    public Result getUsers(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return difyApiService.getUsers(pageNum, pageSize);
    }

    @GetMapping("/users/{id}")
    public Result getUserById(@PathVariable Integer id) {
        return difyApiService.getUserById(id);
    }

    @PutMapping("/users/{id}/points")
    public Result updateUserPoints(@PathVariable Integer id, @RequestParam Integer points) {
        return difyApiService.updateUserPoints(id, points);
    }

    // ==================== 活动 ====================

    @GetMapping("/activities")
    public Result getActivities(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return difyApiService.getActivities(pageNum, pageSize);
    }

    // ==================== 栏目 ====================

    @GetMapping("/columns")
    public Result getColumns() {
        return difyApiService.getColumns();
    }

    // ==================== 评论 ====================

    @GetMapping("/comments")
    public Result getComments(@RequestParam Integer articleId,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return difyApiService.getCommentsByArticleId(articleId, pageNum, pageSize);
    }

    @PutMapping("/comments/{id}/audit")
    public Result auditComment(@PathVariable Integer id, @RequestParam Integer auditStatus) {
        return difyApiService.auditComment(id, auditStatus);
    }

    // ==================== 统计（Dify数据问答核心端点） ====================

    @GetMapping("/statistics")
    public Result getStatistics() {
        return difyApiService.getStatistics();
    }

    /**
     * 按时间范围统计文章/活动
     * Dify HTTP请求节点调用示例：
     *   GET /api/dify/stats/date-range?category=all&startDate=2026-01-01&endDate=2026-05-13
     */
    @GetMapping("/stats/date-range")
    public Result getStatsByDateRange(@RequestParam String category,
                                      @RequestParam String startDate,
                                      @RequestParam String endDate) {
        return difyApiService.getStatsByDateRange(category, startDate, endDate);
    }

    /**
     * 按时间范围查询文章列表
     */
    @GetMapping("/articles/date-range")
    public Result getArticlesByDateRange(@RequestParam String startDate,
                                         @RequestParam String endDate) {
        return difyApiService.getArticlesByDateRange(startDate, endDate);
    }

    /**
     * 按时间范围查询活动列表
     */
    @GetMapping("/activities/date-range")
    public Result getActivitiesByDateRange(@RequestParam String startDate,
                                           @RequestParam String endDate) {
        return difyApiService.getActivitiesByDateRange(startDate, endDate);
    }

    // ==================== 敏感词检测 ====================

    /**
     * 文本敏感词检测
     * POST /api/dify/audit/sensitive-words
     * Body: { "text": "待审核的文本内容" }
     */
    @PostMapping("/audit/sensitive-words")
    public Result checkSensitiveWords(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        if (text == null || text.trim().isEmpty()) {
            return Result.error("400", "请提供待审核的文本内容");
        }
        return difyApiService.checkSensitiveWords(text);
    }

    // ==================== Dify 专用端点（API Key 鉴权，绕过登录） ====================

    /**
     * 提供给Dify工作流调用的内部端点（使用API Key鉴权，不依赖用户Session）
     * 在 Dify HTTP请求节点中配置 Header: X-Api-Key: <your-key>
     */
    @PostMapping("/internal/statistics")
    public Result internalGetStatistics(@RequestHeader(value = "X-Api-Key", required = false) String apiKey) {
        validateApiKey(apiKey);
        return difyApiService.getStatistics();
    }

    @PostMapping("/internal/stats/date-range")
    public Result internalGetStatsByDateRange(@RequestHeader(value = "X-Api-Key", required = false) String apiKey,
                                              @RequestBody Map<String, String> body) {
        validateApiKey(apiKey);
        String category = body.getOrDefault("category", "all");
        String startDate = body.get("startDate");
        String endDate = body.get("endDate");
        if (startDate == null || endDate == null) {
            return Result.error("400", "请提供 startDate 和 endDate");
        }
        return difyApiService.getStatsByDateRange(category, startDate, endDate);
    }

    /**
     * Dify 查询工作流专用：按时间范围统计（GET 方式，Query String 传参）
     * Dify HTTP 请求节点调用：
     *   GET /api/dify/internal/statistics/by-time?startDate=2026-01-01&endDate=2026-05-16&category=articles
     * 鉴权：优先 X-Api-Key，其次 JWT Authorization header
     */
    @GetMapping("/internal/statistics/by-time")
    public Result internalGetStatsByTime(@RequestHeader(value = "X-Api-Key", required = false) String apiKey,
                                         @RequestParam String startDate,
                                         @RequestParam String endDate,
                                         @RequestParam(defaultValue = "all") String category) {
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            validateApiKey(apiKey);
        }
        // 使用内部版方法（不强制要求登录态，已通过 API Key 或路径白名单鉴权）
        return difyApiService.getStatsByDateRangeInternal(category, startDate, endDate);
    }

    @PostMapping("/internal/audit/sensitive-words")
    public Result internalCheckSensitiveWords(@RequestHeader(value = "X-Api-Key", required = false) String apiKey,
                                              @RequestBody Map<String, String> body) {
        validateApiKey(apiKey);
        String text = body.get("text");
        if (text == null || text.trim().isEmpty()) {
            return Result.error("400", "请提供待审核的文本内容");
        }
        return difyApiService.checkSensitiveWords(text);
    }

    /**
     * 聊天代理端点（后端代理模式，传递用户身份给 Dify）
     * 前端 POST /api/dify/chat { "query": "用户消息", "conversationId": "可选" }
     * Header: Authorization: Bearer <JWT Token>
     * 
     * 后端从 JWT 中提取用户身份，注入到 Dify inputs.sys_token，
     * 同时注入 sys_api_key（内部 API Key），Dify 工作流可携带这些信息调用内部 API。
     */
    @PostMapping("/chat")
    public ResponseEntity<String> chatProxy(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        
        String query = body.get("query");
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\":\"请输入消息内容\"}");
        }

        // 从请求 Header 中提取 JWT Token
        String authHeader = request.getHeader("Authorization");
        String userToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            userToken = authHeader.substring(7);  // 去掉 "Bearer " 前缀
        }

        // 获取内部 API Key
        String apiKey = difyApiService.getConfigApiKey();
        String conversationId = body.get("conversationId");

        // 代理调用 Dify Chat API（SSE 流式响应）
        String difyResponse = difyApiService.callDifyChatApi(query, userToken, apiKey, conversationId);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(difyResponse);
    }

    /**
     * 统一操作执行端点（Dify 系统操作分支专用）
     * 
     * Dify 中只需配置一个 HTTP 节点，固定 URL：
     *   POST {{BASE_URL}}/api/dify/internal/action-execute
     * 
     * LLM 输出 JSON 由 Dify 透传即可：
     * {
     *   "entity": "article" | "user" | "activity" | "column" | "comment" | "statistics",
     *   "operation": "list" | "get" | "create" | "update" | "delete" | "search" | "audit" | "stats",
     *   "targetId": 123,       // 可选，get/update/delete 时需要
     *   "params": { ... }      // 可选，create/update/search/stats 时需要
     * }
     */
    @PostMapping("/internal/action-execute")
    public Result executeAction(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        // API Key 可选：有则校验，无则用 JWT Authorization Bearer 鉴权
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            validateApiKey(apiKey);
        } else {
            authenticateViaJwt(request);
        }
        
        String entity = (String) body.getOrDefault("entity", "");
        String operation = (String) body.getOrDefault("operation", "");
        Object targetIdObj = body.get("targetId");
        Integer targetId = parseTargetId(targetIdObj);
        // params 可能被 Dify 参数提取器输出为字符串（JSON串或空串）而非对象，兼容处理
        @SuppressWarnings("unchecked")
        Map<String, Object> params;
        Object rawParams = body.get("params");
        if (rawParams instanceof Map) {
            params = (Map<String, Object>) rawParams;
        } else if (rawParams instanceof String && !((String) rawParams).trim().isEmpty()) {
            try {
                params = cn.hutool.json.JSONUtil.parseObj((String) rawParams);
            } catch (Exception e) {
                log.warn("[DifyAPI] params JSON 解析失败: {}", rawParams);
                params = new HashMap<>();
            }
        } else {
            params = new HashMap<>();
        }
        
        log.info("[DifyAPI] action-execute: entity={}, operation={}, targetId={}, params={}", 
                entity, operation, targetId, params);
        
        return difyApiService.executeAction(entity, operation, targetId, params);
    }

    /** 兼容 Dify 参数提取器输出的 targetId：可能是 null、数字、字符串数字、空字符串 */
    private Integer parseTargetId(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        String s = obj.toString().trim();
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
    }

    /**
     * 从 Authorization: Bearer <jwt> 中解析用户身份并写入 SecurityContext
     * 使后续 service 层的 getCurrentUser() 能正常获取当前用户
     */
    private void authenticateViaJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            return; // 无 JWT，不设登录态（读操作允许匿名）
        }
        String token = authHeader.substring(7);
        try {
            String userId = JWT.decode(token).getAudience().get(0);
            User user = userService.selectByUid(Integer.parseInt(userId));
            if (user == null) {
                throw new ServiceException("401", "用户不存在");
            }
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(user.getPsw())).build();
            verifier.verify(token);

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user.getUname(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.warn("[DifyAPI] JWT 鉴权失败: {}", e.getMessage());
            throw new ServiceException("401", "token无效或已失效");
        }
    }

    /**
     * 统一 API Key 校验
     */
    private void validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new ServiceException("401", "缺少 X-Api-Key 请求头");
        }
        if (!difyApiService.validateApiKey(apiKey)) {
            throw new ServiceException("403", "API Key 无效");
        }
    }
}