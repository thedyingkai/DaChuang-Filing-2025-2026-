package com.example.dangjian_spring.filter;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.dangjian_spring.common.Result;
import com.example.dangjian_spring.entity.User;
import com.example.dangjian_spring.service.UserService;
import com.example.dangjian_spring.utils.PermissionChecker;
import com.example.dangjian_spring.utils.PermissionConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final String ERROR_CODE_401 = "401";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private UserService userService;

    private static final List<String> PUBLIC_INTERFACES = Arrays.asList(
            "/login", "/register", "/file/download/**", "/column/selectAllToShow","/article/selectArticleBy**","user/setgid**",
            "/api/dify/internal/"
    );

    /**
     * 鉴权失败时直接返回 JSON（HTTP 401），不抛异常，避免未经过全局异常处理的 HTML 错误页；
     * 响应体为明文 JSON，不经 {@link EncryptionFilter} 加密，与前端 axios 对 401 分支解析一致。
     */
    private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        OBJECT_MAPPER.writeValue(response.getOutputStream(), Result.error(ERROR_CODE_401, message));
    }

    private static String safeTokenPrefix(String token) {
        if (token == null || token.isEmpty()) {
            return "";
        }
        int n = Math.min(12, token.length());
        return token.substring(0, n) + "...";
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("requestURI: {}", requestURI);
        for (String publicInterface : PUBLIC_INTERFACES) {
            if (requestURI.startsWith(publicInterface.replace("**", ""))) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            // 也支持标准 Authorization: Bearer <token> 格式
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }

        if (StrUtil.isBlank(token)) {
            respondUnauthorized(response, "无token，请重新登录");
            return;
        }

        String userId;
        User user;
        try {
            userId = JWT.decode(token).getAudience().get(0);
            user = userService.selectByUid(Integer.parseInt(userId));
        } catch (Exception e) {
            log.warn("JWT decode or user load failed, tokenPrefix={}, detail={}", safeTokenPrefix(token), e.toString());
            respondUnauthorized(response, "token无效，请重新登录");
            return;
        }
        if (user == null) {
            respondUnauthorized(response, "用户不存在，请重新登录");
            return;
        }

        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPsw())).build();
            jwtVerifier.verify(token);
        } catch (TokenExpiredException e) {
            log.debug("JWT expired, userId={}", userId);
            respondUnauthorized(response, "登录已过期，请重新登录");
            return;
        } catch (JWTVerificationException e) {
            log.warn("JWT verify failed, userId={}, tokenPrefix={}, detail={}", userId, safeTokenPrefix(token), e.toString());
            respondUnauthorized(response, "token无效或已失效，请重新登录");
            return;
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (PermissionChecker.hasPermission(user, PermissionConstants.HOME_MANAGEMENT)) {
            authorities.add(new SimpleGrantedAuthority("HOME_MANAGEMENT"));
        }
        if (PermissionChecker.hasPermission(user, PermissionConstants.COLUMN_CREATION)) {
            authorities.add(new SimpleGrantedAuthority("COLUMN_CREATION"));
        }
        if (PermissionChecker.hasPermission(user, PermissionConstants.ARTICLE_REVIEW)) {
            authorities.add(new SimpleGrantedAuthority("ARTICLE_REVIEW"));
        }
        if (PermissionChecker.hasPermission(user, PermissionConstants.ARTICLE_PUBLISH)) {
            authorities.add(new SimpleGrantedAuthority("DRAFT_EDITOR"));
        }
        if (PermissionChecker.hasPermission(user, PermissionConstants.ACTIVITY_MANAGEMENT)) {
            authorities.add(new SimpleGrantedAuthority("ACTIVITY_MANAGEMENT"));
        }
        if (PermissionChecker.hasPermission(user, PermissionConstants.QUIZ)) {
            authorities.add(new SimpleGrantedAuthority("QUIZ"));
        }
        if (PermissionChecker.hasPermission(user, PermissionConstants.USER_MANAGEMENT)) {
            authorities.add(new SimpleGrantedAuthority("USER_MANAGEMENT"));
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getUname(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
