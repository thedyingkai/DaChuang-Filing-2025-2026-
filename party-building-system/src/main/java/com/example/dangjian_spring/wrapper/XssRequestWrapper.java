package com.example.dangjian_spring.wrapper;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.dangjian_spring.utils.CustomSafelist;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(XssRequestWrapper.class);

    private final Map<String, String> jsonParams = new HashMap<>();

    public XssRequestWrapper(HttpServletRequest request, byte[] requestData) {
        super(request);
        String contentType = request.getContentType();
        if (contentType != null && contentType.startsWith("application/json")) {
            String json = new String(requestData, StandardCharsets.UTF_8);
            if (!json.isEmpty()) {
                try {
                    // 尝试解析为JSONObject
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    if (jsonObject != null) {
                        processJsonObject(jsonObject);
                    }
                } catch (Exception e) {
                    try {
                        // 若解析为JSONObject失败，尝试解析为JSONArray
                        JSONArray jsonArray = JSONArray.parseArray(json);
                        if (jsonArray != null) {
                            processJsonArray(jsonArray);
                        }
                    } catch (Exception ex) {
                        log.debug("JSON body parse for XSS failed", ex);
                    }
                }
            }
        }
    }

    private void processJsonObject(JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                jsonParams.put(key, stripXss((String) value));
            } else if (value instanceof JSONObject) {
                processJsonObject((JSONObject) value);
            } else if (value instanceof JSONArray) {
                processJsonArray((JSONArray) value);
            }
        }
    }

    private void processJsonArray(JSONArray jsonArray) {
        for (Object item : jsonArray) {
            if (item instanceof String) {
                jsonParams.put(String.valueOf(jsonArray.indexOf(item)), stripXss((String) item));
            } else if (item instanceof JSONObject) {
                processJsonObject((JSONObject) item);
            } else if (item instanceof JSONArray) {
                processJsonArray((JSONArray) item);
            }
        }
    }

    /** @return 清洗后的字符串；入参为 {@code null} 时返回 {@code null}（与 {@link HttpServletRequest#getParameter} 约定一致）。 */
    private String stripXss(String value) {
        if (value != null) {
            return Jsoup.clean(value, CustomSafelist.getCustomSafelist());
        }
        return null;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (jsonParams.containsKey(name)) {
            return jsonParams.get(name);
        }
        return stripXss(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                values[i] = stripXss(values[i]);
            }
        }
        return values;
    }
}