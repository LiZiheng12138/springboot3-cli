package com.example.demo.auth;

import com.example.demo.dao.SysUserDao;
import com.example.demo.entity.SysUser;
import com.example.demo.handle.SecurityAuthHandlers;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 注：如果不需要业务系统自行处理认证授权逻辑，无需该类
 */

//@Component
//@RequiredArgsConstructor
//@Slf4j
public class BizJwtAuthenticationFilter extends OncePerRequestFilter {

    private final BizJwtService bizJwtService;
    private final SysUserDao sysUserDao;
    private final SecurityAuthHandlers.JwtAuthenticationEntryPoint entryPoint;

    private static final List<String> IGNORE_URLS = Arrays.asList(
            "/user/auth/callback",
            "/public/",
            "/swagger-ui/",
            "/v3/api-docs",
            "/.well-known/appspecific/com.chrome.devtools.json",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 放行白名单
        for (String ignore : IGNORE_URLS) {
            if (uri.startsWith(ignore)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("access_token".equals(c.getName())) accessToken = c.getValue();
                if ("refresh_token".equals(c.getName())) refreshToken = c.getValue();
            }
        }

        String tokenToUse = null;

        // 1️⃣ access_token 有效
        if (accessToken != null) {
            BizJwtService.TokenStatus status = bizJwtService.checkTokenStatus(accessToken);
            log.info("cookie中的token状态{}", status);
            if (status == BizJwtService.TokenStatus.VALID) {
                tokenToUse = accessToken;
            } else if (status == BizJwtService.TokenStatus.EXPIRED && refreshToken != null) {
                // 2️⃣ access_token 过期 → 尝试刷新
                Claims refreshClaims = bizJwtService.parseToken(refreshToken);
                if (refreshClaims != null) {
                    String userId = refreshClaims.getSubject();
                    SysUser user = sysUserDao.selectOneByUserId(userId);
                    if (user != null) {
                        String newAccessToken = bizJwtService.refreshAccessToken(refreshToken, user);
                        String newRefreshToken = bizJwtService.refreshRefreshToken(refreshToken, user);
                        log.info("刷新access_token：{}", newAccessToken);
                        log.info("刷新refresh_token：{}", newRefreshToken);
                        addCookie(response, "access_token", newAccessToken, 3600);
                        addCookie(response, "refresh_token", newRefreshToken, 3600 * 24);
                        tokenToUse = newAccessToken;
                    }
                }
            }
        }

        // 3️⃣ token 有效 → 设置 SecurityContext
        if (tokenToUse != null) {
            UserDetails userDetails = bizJwtService.getUserDetailsFromToken(tokenToUse);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 4️⃣ 都无效 → 返回 401
        entryPoint.commence(request, response,
                new AccountExpiredException("登录已过期，请重新登录"));
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // 生产环境必须
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}