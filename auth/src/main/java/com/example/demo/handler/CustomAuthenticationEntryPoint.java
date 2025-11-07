package com.example.demo.handler;

import com.example.demo.entity.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String FRONT_LOGIN_URL = "http://localhost:8003/#/login"; // ✅ 前端登录页

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String uri = request.getRequestURI();

        // ✅ 放行 Chrome 探测请求，防止异常日志
        if (uri.startsWith("/.well-known")) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // ✅ OAuth2 授权请求，重定向前端登录页
        if (uri.startsWith("/oauth2/authorize")) {
            response.sendRedirect(FRONT_LOGIN_URL);
            return;
        }

        // ✅ 普通 API 调用，返回 JSON
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                Result.fail(401, "未登录或登录已过期")));
    }
}