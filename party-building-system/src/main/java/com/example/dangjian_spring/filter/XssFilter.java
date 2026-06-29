package com.example.dangjian_spring.filter;

import java.io.IOException;

import com.example.dangjian_spring.wrapper.XssRequestWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class XssFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            // SSE 流式端点不解析 body，避免消耗 InputStream 导致 @RequestBody 失败
            // Dify 工作流 API 也跳过 XSS 过滤（Dify 发送的是明文 JSON，且 EncryptionFilter 已跳过）
            String uri = httpRequest.getRequestURI();
            if (uri.startsWith("/api/dify/") || uri.startsWith("/file/download/") || uri.equals("/file/upload")) {
                chain.doFilter(request, response);
                return;
            }
            String contentType = httpRequest.getContentType();
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                // 如果是 multipart/form-data 类型的数据，直接放行
                chain.doFilter(request, response);
            } else {
                // 读取请求体数据
                byte[] requestData = httpRequest.getInputStream().readAllBytes();
                // 创建 XssRequestWrapper 包装请求
                XssRequestWrapper xssRequestWrapper = new XssRequestWrapper(httpRequest, requestData);
                // 继续执行过滤链，使用包装后的请求
                chain.doFilter(xssRequestWrapper, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化操作，这里可以留空
    }

    @Override
    public void destroy() {
        // 销毁操作，这里可以留空
    }
}