package com.example.dangjian_spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Dify集成配置 - 读取 dify-api-config.properties
 * 该文件已加入 .gitignore，不随版本控制提交
 */
@Configuration
@ConfigurationProperties(prefix = "dify.api")
@PropertySource(value = "classpath:dify-api-config.properties")
public class DifyApiConfig {

    /** 是否启用 Dify API 模块 */
    private boolean enabled = true;

    /** 是否记录 Dify 请求日志 */
    private boolean logRequests = true;

    /** Dify回调用的后端地址（HTTP，供文档参考） */
    private String baseUrl = "http://localhost:8081";

    /** API Key 认证模式（用于Dify直连，绕过登录流程） */
    private String apiKey = "";

    /** 审核模式：local（本地敏感词库匹配）/ dify（委托Dify LLM） */
    private String auditMode = "local";

    /** 本地敏感词列表（逗号分隔），仅在 auditMode=local 时生效 */
    private String sensitiveWords = "";

    /** Dify Chat API 基础地址（Chatflow 应用的 API 端点，如 http://dify.xxx.com/v1） */
    private String difyBaseUrl = "http://localhost:8000";

    /** Dify Chatflow API Key（应用密钥） */
    private String difyApiKey = "";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLogRequests() {
        return logRequests;
    }

    public void setLogRequests(boolean logRequests) {
        this.logRequests = logRequests;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAuditMode() {
        return auditMode;
    }

    public void setAuditMode(String auditMode) {
        this.auditMode = auditMode;
    }

    public String getSensitiveWords() {
        return sensitiveWords;
    }

    public void setSensitiveWords(String sensitiveWords) {
        this.sensitiveWords = sensitiveWords;
    }

    public String getDifyBaseUrl() {
        return difyBaseUrl;
    }

    public void setDifyBaseUrl(String difyBaseUrl) {
        this.difyBaseUrl = difyBaseUrl;
    }

    public String getDifyApiKey() {
        return difyApiKey;
    }

    public void setDifyApiKey(String difyApiKey) {
        this.difyApiKey = difyApiKey;
    }
}
