package com.example.dangjian_spring.config;

import com.example.dangjian_spring.utils.XssHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ThreadPoolTaskExecutor asyncTaskExecutor;

    public WebMvcConfig(ThreadPoolTaskExecutor asyncTaskExecutor) {
        this.asyncTaskExecutor = asyncTaskExecutor;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(asyncTaskExecutor);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new XssHttpMessageConverter());
    }
}