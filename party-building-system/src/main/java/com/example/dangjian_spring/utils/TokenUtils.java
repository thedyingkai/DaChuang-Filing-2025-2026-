package com.example.dangjian_spring.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.dangjian_spring.dao.mapper.User1Mapper;
import com.example.dangjian_spring.dao.mapper.User2Mapper;
import com.example.dangjian_spring.entity.User;
import com.example.dangjian_spring.entity.User2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

@Component
@Slf4j
public class TokenUtils {

    private static User1Mapper staticUserMapper;

    private static User2Mapper staticUser2Mapper;

    private static int jwtExpireHours = 2;

    @Resource
    User1Mapper user1Mapper;
    @Resource
    User2Mapper user2Mapper;

    @Value("${jwt.expire-hours:2}")
    private int jwtExpireHoursConfig;

    @PostConstruct
    public void initStaticDependencies() {
        staticUserMapper = user1Mapper;
        staticUser2Mapper = user2Mapper;
        jwtExpireHours = jwtExpireHoursConfig;
    }

    /**
     * 生成token
     *
     * @return
     */
    public static String createToken(String userId, String sign) {
        return JWT.create().withAudience(userId) // 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), jwtExpireHours))
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    public static String createToken2(String userId, String sign)
    {
        return JWT.create().withAudience(userId) // 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), jwtExpireHours))
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    /**
     * 从请求上下文解析当前登录用户（user_view）。无 token 或解析失败时返回 {@code null}，调用方须判空。
     */
    public static User getCurrentUser1() {
        String token = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            token = request.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                String uid = JWT.decode(token).getAudience().get(0);
                return staticUserMapper.selectByUid(Integer.valueOf(uid));
            }
        } catch (Exception e) {
            log.error("获取当前 user_view 登录用户失败, tokenPrefix={}", safeTokenPrefix(token), e);
            return null;
        }
        return null;
    }

    /** 与 {@link #getCurrentUser1()} 相同约定；返回 {@link User2}。 */
    public static User2 getCurrentUser2() {
        String token = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            token = request.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                String uid = JWT.decode(token).getAudience().get(0);
                return staticUser2Mapper.selectUserById(Integer.parseInt(uid));
            }
        } catch (Exception e) {
            log.error("获取当前 User2 登录用户失败, tokenPrefix={}", safeTokenPrefix(token), e);
            return null;
        }
        return null;
    }

    private static String safeTokenPrefix(String token) {
        if (token == null || token.isEmpty()) {
            return "";
        }
        int n = Math.min(12, token.length());
        return token.substring(0, n) + "...";
    }
}
