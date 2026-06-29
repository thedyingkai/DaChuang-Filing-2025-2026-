package com.example.dangjian_spring.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.config.DifyApiConfig;
import com.example.dangjian_spring.dao.mapper.*;
import com.example.dangjian_spring.entity.*;
import com.example.dangjian_spring.exception.ServiceException;
import com.example.dangjian_spring.utils.PermissionChecker;
import com.example.dangjian_spring.utils.PermissionConstants;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

/**
 * Dify工作流集成专用Service
 * 读操作：需登录Token
 * 写操作：需登录Token + 管理员身份（cid=1 或 USER_MANAGEMENT权限）
 */
@Service
@Slf4j
public class DifyApiService {

    @Resource
    private UserService userService;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private DraftMapper draftMapper;
    @Resource
    private User1Mapper user1Mapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ColumnMapper columnMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private DifyApiConfig difyApiConfig;

    // ==================== 鉴权工具方法 ====================

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new ServiceException("401", "未登录，请提供有效Token");
        }
        String uname = auth.getPrincipal().toString();
        User user = userService.selectByUname(uname);
        if (user == null) {
            throw new ServiceException("401", "用户不存在");
        }
        return user;
    }

    private boolean isAdmin(User user) {
        if (user.getCid() != null && user.getCid() == 1) {
            return true;
        }
        return PermissionChecker.hasPermission(user, PermissionConstants.USER_MANAGEMENT);
    }

    private void requireAdmin(User user) {
        if (!isAdmin(user)) {
            throw new ServiceException("403", "权限不足：仅系统管理员可执行此操作");
        }
    }

    private void logRequest(String operation, Object params) {
        if (difyApiConfig.isLogRequests()) {
            log.info("[DifyAPI] op={}, params={}", operation, params);
        }
    }

    // ==================== 文章相关 ====================

    public Result searchArticlesByTitle(String titleKeyword) {
        getCurrentUser();
        logRequest("searchArticlesByTitle", titleKeyword);
        List<Article> list = articleMapper.selectByTitle(titleKeyword);
        return Result.success(list);
    }

    public Result getArticles(Integer pageNum, Integer pageSize) {
        getCurrentUser();
        logRequest("getArticles", Map.of("pageNum", pageNum, "pageSize", pageSize));
        Page<Article> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        articleMapper.selectPage(page, new QueryWrapper<Article>());
        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotal());
        result.put("pages", page.getPages());
        result.put("current", page.getCurrent());
        result.put("records", page.getRecords());
        return Result.success(result);
    }

    public Result getArticleById(Integer id) {
        getCurrentUser();
        logRequest("getArticleById", id);
        Article article = articleMapper.selectByid(id);
        if (article == null) {
            return Result.error("404", "文章不存在");
        }
        return Result.success(article);
    }

    /**
     * 通过 Dify AI 创建文章 → 先存入草稿箱（draft），走正常审核流程，不再直写 article 表。
     * 用户可在前端「信息发布 → 草稿箱」中找到该草稿，手动提交审核后由审核人发布。
     */
    public Result createArticle(Article article) {
        User user = getCurrentUser();
        // Dify AI 发布不要求管理员权限，普通用户即可创建草稿
        logRequest("createArticle(draft)", article);
        if (article.getCoid() == null || article.getTitle() == null) {
            return Result.error("400", "栏目(coid)和标题(title)不能为空");
        }

        // 构造 Draft 对象，存入草稿箱
        Draft draft = new Draft();
        draft.setUid(user.getId());
        draft.setCoid(article.getCoid());
        draft.setTitle(article.getTitle());
        draft.setContent(article.getContent());
        draft.setSource(article.getSource());
        draft.setSave_time(DateUtil.now());
        // status 默认为 0（草稿状态），用户可在草稿箱中手动提交

        int rows = draftMapper.insert(draft);
        if (rows > 0) {
            log.info("[DifyAPI] 草稿创建成功: draftId={}, title={}, uid={}", draft.getId(), draft.getTitle(), user.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("draftId", draft.getId());
            result.put("title", draft.getTitle());
            result.put("coid", draft.getCoid());
            result.put("message", "文章已存入草稿箱，请前往「信息发布 → 草稿箱」提交审核");
            return Result.success(result);
        }
        return Result.error("500", "创建草稿失败");
    }

    public Result updateArticle(Integer id, Article article) {
        User user = getCurrentUser();
        requireAdmin(user);
        logRequest("updateArticle", Map.of("id", id, "article", article));
        Article existing = articleMapper.selectByid(id);
        if (existing == null) {
            return Result.error("404", "文章不存在");
        }
        article.setId(id);
        int rows = articleMapper.updateById(article);
        return rows > 0 ? Result.success(article) : Result.error("500", "更新文章失败");
    }

    // ==================== 用户相关 ====================

    public Result getUsers(Integer pageNum, Integer pageSize) {
        getCurrentUser();
        logRequest("getUsers", Map.of("pageNum", pageNum, "pageSize", pageSize));
        Page<User> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        user1Mapper.selectPage(page, new QueryWrapper<User>());
        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotal());
        result.put("pages", page.getPages());
        result.put("current", page.getCurrent());
        result.put("records", page.getRecords());
        return Result.success(result);
    }

    public Result getUserById(Integer id) {
        getCurrentUser();
        logRequest("getUserById", id);
        User user = userService.selectByUid(id);
        if (user == null) {
            return Result.error("404", "用户不存在");
        }
        return Result.success(user);
    }

    public Result updateUserPoints(Integer id, Integer points) {
        User user = getCurrentUser();
        requireAdmin(user);
        logRequest("updateUserPoints", Map.of("id", id, "points", points));
        User target = userService.selectByUid(id);
        if (target == null) {
            return Result.error("404", "用户不存在");
        }
        target.setPoints(points);
        user1Mapper.updateById(target);
        return Result.success(target);
    }

    // ==================== 活动相关 ====================

    public Result getActivities(Integer pageNum, Integer pageSize) {
        getCurrentUser();
        logRequest("getActivities", Map.of("pageNum", pageNum, "pageSize", pageSize));
        Page<Activity> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        activityMapper.selectPage(page, new QueryWrapper<Activity>());
        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotal());
        result.put("pages", page.getPages());
        result.put("current", page.getCurrent());
        result.put("records", page.getRecords());
        return Result.success(result);
    }

    // ==================== 栏目相关 ====================

    public Result getColumns() {
        getCurrentUser();
        logRequest("getColumns", null);
        List<Column> list = columnMapper.selectAll();
        return Result.success(list);
    }

    // ==================== 评论相关 ====================

    public Result getCommentsByArticleId(Integer articleId, Integer pageNum, Integer pageSize) {
        getCurrentUser();
        logRequest("getCommentsByArticleId", Map.of("articleId", articleId, "pageNum", pageNum, "pageSize", pageSize));
        Page<Comment> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("aid", articleId);
        commentMapper.selectPage(page, wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotal());
        result.put("pages", page.getPages());
        result.put("current", page.getCurrent());
        result.put("records", page.getRecords());
        return Result.success(result);
    }

    public Result auditComment(Integer id, Integer auditStatus) {
        User user = getCurrentUser();
        requireAdmin(user);
        logRequest("auditComment", Map.of("id", id, "auditStatus", auditStatus));
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Result.error("404", "评论不存在");
        }
        if (auditStatus == null || (auditStatus != 0 && auditStatus != 1)) {
            return Result.error("400", "审核状态必须为 0(未通过) 或 1(已通过)");
        }
        comment.setStatus(auditStatus);
        commentMapper.updateById(comment);
        return Result.success(comment);
    }

    // ==================== 统计相关 ====================

    public Result getStatistics() {
        getCurrentUser();
        logRequest("getStatistics", null);
        Map<String, Object> stats = new HashMap<>();
        List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>());
        stats.put("articleCount", articles != null ? articles.size() : 0);
        List<User> users = user1Mapper.selectList(new QueryWrapper<User>());
        stats.put("userCount", users != null ? users.size() : 0);
        List<Activity> activities = activityMapper.selectList(new QueryWrapper<Activity>());
        stats.put("activityCount", activities != null ? activities.size() : 0);
        List<Column> columns = columnMapper.selectList(new QueryWrapper<Column>());
        stats.put("columnCount", columns != null ? columns.size() : 0);
        return Result.success(stats);
    }

    /**
     * 按时间范围统计（Dify数据问答专用）
     */
    public Result getStatsByDateRange(String category, String startDate, String endDate) {
        getCurrentUser();
        return doGetStatsByDateRange(category, startDate, endDate);
    }

    /**
     * 按时间范围统计（内部调用版，不要求登录态）
     * 供 /api/dify/internal/statistics/by-time 等内部端点使用
     */
    public Result getStatsByDateRangeInternal(String category, String startDate, String endDate) {
        logRequest("getStatsByDateRange(internal)", Map.of("category", category, "startDate", startDate, "endDate", endDate));
        return doGetStatsByDateRange(category, startDate, endDate);
    }

    private Result doGetStatsByDateRange(String category, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("category", category);
        result.put("startDate", startDate);
        result.put("endDate", endDate);

        if ("article".equals(category) || "articles".equals(category) || "all".equals(category)) {
            int articleCount = articleMapper.countByDateRange(startDate, endDate);
            result.put("articleCount", articleCount);
            List<Map<String, Object>> columnStats = articleMapper.countByColumnAndDateRange(startDate, endDate);
            result.put("articleByColumn", columnStats);
        }
        if ("activity".equals(category) || "activities".equals(category) || "all".equals(category)) {
            int activityCount = activityMapper.countByDateRange(startDate, endDate);
            result.put("activityCount", activityCount);
            List<Map<String, Object>> typeStats = activityMapper.countByTypeAndDateRange(startDate, endDate);
            result.put("activityByType", typeStats);
        }
        return Result.success(result);
    }

    public Result getArticlesByDateRange(String startDate, String endDate) {
        getCurrentUser();
        logRequest("getArticlesByDateRange", Map.of("startDate", startDate, "endDate", endDate));
        List<Article> list = articleMapper.selectByDateRange(startDate, endDate);
        return Result.success(list);
    }

    public Result getActivitiesByDateRange(String startDate, String endDate) {
        getCurrentUser();
        logRequest("getActivitiesByDateRange", Map.of("startDate", startDate, "endDate", endDate));
        List<Activity> list = activityMapper.selectByDateRange(startDate, endDate);
        return Result.success(list);
    }

    // ==================== 敏感词 / 审核辅助 ====================

    public Result checkSensitiveWords(String text) {
        getCurrentUser();
        logRequest("checkSensitiveWords", Map.of("textLen", text != null ? text.length() : 0));
        if (text == null || text.trim().isEmpty()) {
            return Result.success(Map.of("matched", false, "message", "文本为空"));
        }
        String mode = difyApiConfig.getAuditMode();
        if ("local".equals(mode)) {
            return checkSensitiveWordsLocal(text);
        }
        return Result.success(Map.of(
            "matched", false,
            "mode", "dify",
            "message", "已委托Dify LLM进行深度审核"
        ));
    }

    private Result checkSensitiveWordsLocal(String text) {
        String wordsConfig = difyApiConfig.getSensitiveWords();
        if (wordsConfig == null || wordsConfig.trim().isEmpty()) {
            return Result.success(Map.of("matched", false, "message", "未配置敏感词库"));
        }
        String lowerText = text.toLowerCase();
        String[] words = wordsConfig.split(",");
        StringBuilder matched = new StringBuilder();
        int count = 0;
        for (String word : words) {
            String w = word.trim();
            if (!w.isEmpty() && lowerText.contains(w.toLowerCase())) {
                if (matched.length() > 0) matched.append(",");
                matched.append(w);
                count++;
            }
        }
        if (count > 0) {
            return Result.success(Map.of(
                "matched", true,
                "count", count,
                "words", matched.toString(),
                "suggestion", "已匹配到 " + count + " 个敏感词，建议修改后再提交。",
                "mode", "local"
            ));
        }
        return Result.success(Map.of("matched", false, "mode", "local", "message", "未检出敏感词"));
    }

    /**
     * 代理调用 Dify Chat API（Chatflow），透传用户消息并注入用户身份 Token
     * @param userMessage 用户输入的自然语言消息
     * @param userToken   当前登录用户的 JWT Token（会被注入到 Dify inputs.sys_token）
     * @param apiKey      内部 API Key（会被注入到 Dify inputs.sys_api_key）
     * @param conversationId 多轮对话 ID（可选，传 null 则开始新对话）
     * @return Dify 返回的 JSON 字符串（SSE 流式或多轮阻塞格式）
     */
    public String callDifyChatApi(String userMessage, String userToken, String apiKey, String conversationId) {
        String difyBaseUrl = difyApiConfig.getDifyBaseUrl();
        String difyApiKey = difyApiConfig.getDifyApiKey();

        if (difyBaseUrl == null || difyBaseUrl.trim().isEmpty()) {
            throw new ServiceException("500", "Dify Chat API 基础地址未配置");
        }
        if (difyApiKey == null || difyApiKey.trim().isEmpty()) {
            throw new ServiceException("500", "Dify Chatflow API Key 未配置");
        }

        // 构造请求体（使用 Hutool JSONObject）
        JSONObject inputs = new JSONObject();
        inputs.set("sys_token", userToken != null ? userToken : "");
        inputs.set("sys_api_key", apiKey != null ? apiKey : "");

        JSONObject body = new JSONObject();
        body.set("inputs", inputs);
        body.set("query", userMessage);
        body.set("response_mode", "streaming");  // 使用流式模式
        body.set("user", "party-building-user");
        if (conversationId != null && !conversationId.trim().isEmpty()) {
            body.set("conversation_id", conversationId);
        }

        // 构造 HTTP 请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + difyApiKey);

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();

        String url = difyBaseUrl + "/chat-messages";
        log.info("[DifyAPI] 代理调用 Dify Chat API: url={}, query={}", url, userMessage);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                log.error("[DifyAPI] Dify 返回非 2xx 状态: {}", response.getStatusCode());
                throw new ServiceException("502", "Dify Chat API 调用失败，状态码: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("[DifyAPI] Dify Chat API 调用异常: {}", e.getMessage(), e);
            throw new ServiceException("502", "Dify Chat API 连接失败: " + e.getMessage());
        }
    }

    /**
     * 统一操作执行入口（Dify 系统操作分支专用）
     * 
     * @param entity    "article" | "user" | "activity" | "column" | "comment" | "statistics"
     * @param operation "list" | "get" | "create" | "update" | "delete" | "search" | "audit" | "stats"
     * @param targetId  可选，get/update/delete 时需要
     * @param params    可选，create/update/search/stats 时需要
     */
    public Result executeAction(String entity, String operation, Integer targetId, Map<String, Object> params) {
        log.info("[DifyAPI] executeAction: entity={}, operation={}, targetId={}", entity, operation, targetId);
        
        switch (entity) {
            case "article":
                return executeArticleAction(operation, targetId, params);
            case "user":
                return executeUserAction(operation, targetId, params);
            case "activity":
                return executeActivityAction(operation, targetId, params);
            case "column":
                return executeColumnAction(operation, targetId, params);
            case "comment":
                return executeCommentAction(operation, targetId, params);
            case "statistics":
                return executeStatisticsAction(operation, params);
            default:
                return Result.error("400", "不支持的实体类型: " + entity + "，支持的类型: article, user, activity, column, comment, statistics");
        }
    }

    // ---- 文章 ----

    private Result executeArticleAction(String operation, Integer targetId, Map<String, Object> params) {
        switch (operation) {
            case "list": {
                int pageNum = getIntParam(params, "pageNum", 1);
                int pageSize = getIntParam(params, "pageSize", 10);
                return getArticles(pageNum, pageSize);
            }
            case "get":
                if (targetId == null) return Result.error("400", "get 操作需要 targetId");
                return getArticleById(targetId);
            case "create": {
                // 复用现有 createArticle 方法，构造 Article 对象
                Article article = new Article();
                article.setCoid(getIntParam(params, "coid", null));
                article.setTitle(getStrParam(params, "title", null));
                article.setContent(getStrParam(params, "content", ""));
                article.setSource(getStrParam(params, "source", ""));
                return createArticle(article);
            }
            case "update":
                if (targetId == null) return Result.error("400", "update 操作需要 targetId");
                // 复用现有 updateArticle 方法
                Article patch = new Article();
                if (params.containsKey("title")) patch.setTitle(getStrParam(params, "title", null));
                if (params.containsKey("content")) patch.setContent(getStrParam(params, "content", null));
                if (params.containsKey("coid")) patch.setCoid(getIntParam(params, "coid", null));
                return updateArticle(targetId, patch);
            case "delete":
                if (targetId == null) return Result.error("400", "delete 操作需要 targetId");
                // 复用 getArticleById + articleMapper.deleteById
                Result getRes = getArticleById(targetId);
                if (!"200".equals(getRes.getCode())) return getRes;
                articleMapper.deleteById(targetId);
                return Result.success(Map.of("deleted", true, "id", targetId));
            case "search":
                return searchArticlesByTitle(getStrParam(params, "keyword", ""));
            default:
                return Result.error("400", "文章不支持的操作: " + operation);
        }
    }

    // ---- 用户 ----

    private Result executeUserAction(String operation, Integer targetId, Map<String, Object> params) {
        switch (operation) {
            case "list": {
                int pageNum = getIntParam(params, "pageNum", 1);
                int pageSize = getIntParam(params, "pageSize", 10);
                return getUsers(pageNum, pageSize);
            }
            case "get": {
                Integer uid = resolveUserId(targetId, params);
                if (uid == null) return Result.error("400", "get 操作需要 targetId 或 params.name");
                return getUserById(uid);
            }
            case "update": {
                Integer uid = resolveUserId(targetId, params);
                if (uid == null) return Result.error("400", "update 操作需要 targetId 或 params.name");
                if (params != null && params.containsKey("points")) {
                    return updateUserPoints(uid, getIntParam(params, "points", 0));
                }
                return Result.error("400", "用户更新目前仅支持修改积分(points)");
            }
            default:
                return Result.error("400", "用户不支持的操作: " + operation);
        }
    }

    /** 解析用户 ID：优先 targetId，其次 params.name 按用户名查 */
    private Integer resolveUserId(Integer targetId, Map<String, Object> params) {
        if (targetId != null) return targetId;
        if (params != null && params.containsKey("name")) {
            String name = getStrParam(params, "name", "");
            if (!name.isEmpty()) {
                User user = userService.selectByUname(name);
                return user != null ? user.getId() : null;
            }
        }
        return null;
    }

    // ---- 活动 ----

    private Result executeActivityAction(String operation, Integer targetId, Map<String, Object> params) {
        switch (operation) {
            case "list": {
                int pageNum = getIntParam(params, "pageNum", 1);
                int pageSize = getIntParam(params, "pageSize", 10);
                return getActivities(pageNum, pageSize);
            }
            case "get":
                if (targetId == null) return Result.error("400", "get 操作需要 targetId (activity id)");
                Activity activity = activityMapper.selectById(targetId);
                return activity != null ? Result.success(activity) : Result.error("404", "活动不存在");
            case "stats":
                return getStatsByDateRange(
                    getStrParam(params, "category", "all"),
                    getStrParam(params, "startDate", ""),
                    getStrParam(params, "endDate", "")
                );
            default:
                return Result.error("400", "活动不支持的操作: " + operation);
        }
    }

    // ---- 栏目 ----

    private Result executeColumnAction(String operation, Integer targetId, Map<String, Object> params) {
        switch (operation) {
            case "list":
                return getColumns();
            case "get":
                if (targetId == null) return Result.error("400", "get 操作需要 targetId (column id)");
                Column column = columnMapper.selectById(targetId);
                return column != null ? Result.success(column) : Result.error("404", "栏目不存在");
            default:
                return Result.error("400", "栏目不支持的操作: " + operation);
        }
    }

    // ---- 评论 ----

    private Result executeCommentAction(String operation, Integer targetId, Map<String, Object> params) {
        switch (operation) {
            case "list":
                // articleId 优先从 params 取，其次用 targetId（Dify 可能把文章ID放 targetId）
                Integer articleId = getIntParam(params, "articleId", null);
                if (articleId == null) articleId = targetId;
                if (articleId == null) return Result.error("400", "list 操作需要 articleId（在 params 或 targetId 中）");
                return getCommentsByArticleId(
                    articleId,
                    getIntParam(params, "pageNum", 1),
                    getIntParam(params, "pageSize", 10)
                );
            case "audit":
                if (targetId == null) return Result.error("400", "audit 操作需要 targetId (comment id)");
                return auditComment(targetId, getIntParam(params, "auditStatus", 1));
            default:
                return Result.error("400", "评论不支持的操作: " + operation);
        }
    }

    // ---- 统计 ----

    private Result executeStatisticsAction(String operation, Map<String, Object> params) {
        switch (operation) {
            case "overview":
                // 内部调用：不要求登录态（/internal/ 路径跳过 JWT，由 controller 的 X-Api-Key 或路径白名单保护）
                return getStatisticsInternal();
            case "dateRange":
                return getStatsByDateRangeInternal(
                    getStrParam(params, "category", "all"),
                    getStrParam(params, "startDate", ""),
                    getStrParam(params, "endDate", "")
                );
            default:
                return Result.error("400", "统计不支持的操作: " + operation);
        }
    }

    /** 获取系统总览统计（内部版，不要求登录态） */
    private Result getStatisticsInternal() {
        logRequest("getStatistics(internal)", null);
        Map<String, Object> stats = new HashMap<>();
        stats.put("articleCount", articleMapper.selectCount(new QueryWrapper<>()));
        stats.put("userCount", user1Mapper.selectCount(new QueryWrapper<>()));
        stats.put("activityCount", activityMapper.selectCount(new QueryWrapper<>()));
        stats.put("columnCount", columnMapper.selectCount(new QueryWrapper<>()));
        return Result.success(stats);
    }

    // ---- 参数提取工具 ----

    private String getStrParam(Map<String, Object> params, String key, String defaultValue) {
        if (params == null || !params.containsKey(key)) return defaultValue;
        Object val = params.get(key);
        return val != null ? val.toString() : defaultValue;
    }

    private Integer getIntParam(Map<String, Object> params, String key, Integer defaultValue) {
        if (params == null || !params.containsKey(key)) return defaultValue;
        Object val = params.get(key);
        if (val instanceof Integer) return (Integer) val;
        if (val instanceof Number) return ((Number) val).intValue();
        if (val instanceof String) {
            try { return Integer.parseInt((String) val); } catch (NumberFormatException e) { return defaultValue; }
        }
        return defaultValue;
    }

    /** 获取配置的 API Key（供 Controller 代理端点使用） */
    public String getConfigApiKey() {
        return difyApiConfig.getApiKey();
    }

    public boolean validateApiKey(String providedKey) {
        String configuredKey = difyApiConfig.getApiKey();
        if (configuredKey == null || configuredKey.trim().isEmpty()) {
            log.warn("[DifyAPI] API Key 未配置，拒绝外部调用");
            return false;
        }
        return configuredKey.equals(providedKey);
    }
}