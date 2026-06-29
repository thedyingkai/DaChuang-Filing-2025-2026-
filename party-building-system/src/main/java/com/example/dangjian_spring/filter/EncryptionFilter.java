package com.example.dangjian_spring.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.example.dangjian_spring.wrapper.EncryptedRequestWrapper;
import com.example.dangjian_spring.wrapper.EncryptedResponseWrapper;
import com.example.dangjian_spring.utils.SM4Utils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class EncryptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 暴露自定义响应头
        httpResponse.setHeader("Access-Control-Expose-Headers", "SM4-Key,SM4-IV");

        if (httpRequest.getRequestURI().startsWith("/file/download/")
                || httpRequest.getRequestURI().startsWith("/file/upload")
                || httpRequest.getRequestURI().startsWith("/file/editor/")
                || httpRequest.getRequestURI().startsWith("/api/dify/")) {
            chain.doFilter(request, response);
            return;
        }

        if (httpRequest.getAttribute("requestProcessed") != null) {
            chain.doFilter(request, response);
            return;
        }

        EncryptedResponseWrapper responseWrapper = new EncryptedResponseWrapper(httpResponse);

        byte[] key;
        byte[] iv;

        byte[] requestData = readRequestData(httpRequest);


        if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
            try {
                key = SM4Utils.generateKey();
                iv = SM4Utils.generateIV();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 使用 Base64 编码
            httpResponse.setHeader("SM4-Key", Base64.getEncoder().encodeToString(key));
            httpResponse.setHeader("SM4-IV", Base64.getEncoder().encodeToString(iv));

            chain.doFilter(request, responseWrapper);

            if (httpResponse.getHeader("Encrypted") != null) {
                byte[] responseData = responseWrapper.getContentAsByteArray();
                httpResponse.setContentLength(responseData.length);
                httpResponse.getOutputStream().write(responseData);
                return;
            }

            responseAndEncrypt(httpResponse, responseWrapper, key, iv);
        } else {
            String keyString = httpRequest.getHeader("SM4-Key");
            String ivString = httpRequest.getHeader("SM4-IV");
            if (keyString == null || ivString == null) {
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // 使用 Base64 解码
            key = Base64.getDecoder().decode(keyString);
            iv = Base64.getDecoder().decode(ivString);

            String encryptedRequestDataString = new String(requestData, StandardCharsets.UTF_8);
            encryptedRequestDataString = encryptedRequestDataString.replaceAll("^\"|\"$", "");
            requestData = encryptedRequestDataString.getBytes(StandardCharsets.UTF_8);

            if (requestData.length == 0) {
                chain.doFilter(request, responseWrapper);
            } else {
                try {
                    byte[] decodedEncryptedData = Base64.getDecoder().decode(requestData);
                    byte[] decryptedRequestData = SM4Utils.decrypt(decodedEncryptedData, key, iv);

                    httpRequest.setAttribute("requestProcessed", true);

                    EncryptedRequestWrapper requestWrapper = new EncryptedRequestWrapper(httpRequest, decryptedRequestData);
                    requestWrapper.setContentType("application/json");
                    httpRequest = requestWrapper;
                    httpRequest.setAttribute("Content-Type", "application/json");

                    chain.doFilter(requestWrapper, responseWrapper);
                } catch (IllegalArgumentException e) {
                    httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }

            try {
                key = SM4Utils.generateKey();
                iv = SM4Utils.generateIV();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 使用 Base64 编码
            httpResponse.setHeader("SM4-Key", Base64.getEncoder().encodeToString(key));
            httpResponse.setHeader("SM4-IV", Base64.getEncoder().encodeToString(iv));

            responseAndEncrypt(httpResponse, responseWrapper, key, iv);
        }
    }

    private void responseAndEncrypt(HttpServletResponse httpResponse, EncryptedResponseWrapper responseWrapper,
                                    byte[] key, byte[] iv) throws IOException {
        byte[] responseData = responseWrapper.getContentAsByteArray();
        byte[] encryptedResponseData;
        try {
            encryptedResponseData = SM4Utils.encrypt(responseData, key, iv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String encodedEncryptedResponseData = Base64.getEncoder().encodeToString(encryptedResponseData);
        byte[] finalResponseData = encodedEncryptedResponseData.getBytes();

        httpResponse.setHeader("Encrypted", "true");
        httpResponse.setContentLength(finalResponseData.length);
        httpResponse.getOutputStream().write(finalResponseData);
    }

    private byte[] readRequestData(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }
}