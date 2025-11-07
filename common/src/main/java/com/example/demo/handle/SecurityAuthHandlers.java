package com.example.demo.handle;

import com.example.demo.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SecurityAuthHandlers {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** 未认证或 Token 无效 */
    @Component
    public static class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void commence(HttpServletRequest request,
                             HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE); // ✅ 返回 JSON
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            log.info("未认证或 Token 无效", authException);
            response.getWriter().write(objectMapper.writeValueAsString(Result.fail(401, "请登录后访问")));
            response.getWriter().flush();
        }
    }

    public static class JwtAccessDeniedHandler implements AccessDeniedHandler {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void handle(HttpServletRequest request,
                           HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws IOException {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE); // ✅ 返回 JSON
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.fail(403, "没有访问权限")));
            response.getWriter().flush();
        }
    }
}