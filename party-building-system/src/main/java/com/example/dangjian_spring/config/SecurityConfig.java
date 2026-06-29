package com.example.dangjian_spring.config;

import com.example.dangjian_spring.filter.EncryptionFilter;
import com.example.dangjian_spring.filter.JwtFilter;
import com.example.dangjian_spring.filter.XssFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final XssFilter xssFilter;
    private final EncryptionFilter encryptionFilter;

    public SecurityConfig(JwtFilter jwtFilter, XssFilter xssFilter, EncryptionFilter encryptionFilter) {
        this.jwtFilter = jwtFilter;
        this.xssFilter = xssFilter;
        this.encryptionFilter = encryptionFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/register", "/file/download/**", "/column/selectAllToShow",
                                "/article/selectArticleBycoid/**", "/article/selectArticleByid/**",
                                "/article/selectArticleByuid/**", "/article/selectArticleBybranch").permitAll()
                        .requestMatchers("/article/moveToColumn/").hasAuthority("HOME_MANAGEMENT")
                        /* 题库维护（QUIZ）需与「栏目设置」同一套栏目树，仅放开只读列表 */
                        .requestMatchers(HttpMethod.GET, "/column/selectAll").hasAnyAuthority("QUIZ", "COLUMN_CREATION")
                        .requestMatchers("/column/**", "/article/batchMoveToColumn/**").hasAuthority("COLUMN_CREATION")
                        .requestMatchers("/article/review/**").hasAuthority("ARTICLE_REVIEW")
                        .requestMatchers("/draft/**").hasAuthority("DRAFT_EDITOR")
                        /* 资源浏览页筛选：只读拉取活动/用户列表，任意登录用户可用 */
                        .requestMatchers(HttpMethod.GET, "/activity/selectAll", "/activity/selectAllExceptDefault")
                        .authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/selectAll").authenticated()
                        .requestMatchers("/activity/**").hasAuthority("ACTIVITY_MANAGEMENT")
                        .requestMatchers(HttpMethod.GET, "/resource/**").authenticated()
                        // 试题/试卷（须同时匹配无子路径的 /papers、/keywords 等）
                        .requestMatchers(
                                "/papers", "/papers/**",
                                "/questions", "/questions/**",
                                "/options", "/options/**",
                                "/keywords", "/keywords/**",
                                "/participation", "/participation/**",
                                "/users", "/users/**",
                                "/columns", "/columns/**",
                                "/role", "/role/**",
                                "/batch", "/batch/**"
                        ).hasAuthority("QUIZ")
                        .requestMatchers("/user/**").hasAuthority("USER_MANAGEMENT")
                        // Dify 工作流集成 API（内部端点使用 X-Api-Key 鉴权，免登录）
                        .requestMatchers("/api/dify/internal/**").permitAll()
                        // Dify 工作流集成 API（用户侧端点，需登录Token）
                        .requestMatchers("/api/dify/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(encryptionFilter, JwtFilter.class)
                .addFilterAfter(xssFilter, EncryptionFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "https://localhost:8080",
            "http://localhost:8080",
            "http://localhost:88",
            "https://localhost:88",
            "https://bt.thedyingkai.cn",
            "https://bt.thedyingkai.cn:8081",
            "https://dangjian.thedyingkai.cn",
            "http://dangjian.thedyingkai.cn",
            "http://dangjian.thedyingkai.cn:88",
            "http://49.232.136.25:88"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(24 * 60 * 60L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}