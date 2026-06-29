package com.example.dangjian_spring.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import com.example.dangjian_spring.utils.CustomSafelist;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class XssHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public XssHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        StringBuilder body = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(inputMessage.getBody(), StandardCharsets.UTF_8)) {
            char[] buffer = new char[1024];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                body.append(buffer, 0, bytesRead);
            }
        }
        String json = body.toString();
        Object jsonData;
        try {
            // 尝试解析为 JSONObject
            jsonData = JSON.parseObject(json);
        } catch (Exception e) {
            try {
                // 若解析为 JSONObject 失败，尝试解析为 JSONArray
                jsonData = JSON.parseArray(json);
            } catch (Exception ex) {
                throw new HttpMessageNotReadableException("Failed to parse JSON data", ex);
            }
        }
        filterXss(jsonData);
        return JSON.parseObject(JSON.toJSONString(jsonData), clazz);
    }

    private void filterXss(Object jsonData) {
        if (jsonData instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) jsonData;
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    jsonObject.put(key, Jsoup.clean((String) value, CustomSafelist.getCustomSafelist()));
                } else if (value instanceof JSONObject) {
                    filterXss(value);
                } else if (value instanceof JSONArray) {
                    filterXss(value);
                }
            }
        } else if (jsonData instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonData;
            for (int i = 0; i < jsonArray.size(); i++) {
                Object item = jsonArray.get(i);
                if (item instanceof String) {
                    jsonArray.set(i, Jsoup.clean((String) item, CustomSafelist.getCustomSafelist()));
                } else if (item instanceof JSONObject) {
                    filterXss(item);
                } else if (item instanceof JSONArray) {
                    filterXss(item);
                }
            }
        }
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // 如果原始对象已经是字符串（如 Dify 代理返回的流式响应），直接写入，不进行 JSON 解析
        if (o instanceof String) {
            try (OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), StandardCharsets.UTF_8)) {
                writer.write((String) o);
            }
            return;
        }

        // 对响应数据进行 XSS 过滤
        String json = JSON.toJSONString(o);
        Object jsonData;
        try {
            jsonData = JSON.parseObject(json);
        } catch (Exception e) {
            try {
                jsonData = JSON.parseArray(json);
            } catch (Exception ex) {
                throw new HttpMessageNotWritableException("Failed to parse JSON data for writing", ex);
            }
        }
        filterXss(jsonData);
        String filteredJson = JSON.toJSONString(jsonData);

        try (OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), StandardCharsets.UTF_8)) {
            writer.write(filteredJson);
        }
    }
}