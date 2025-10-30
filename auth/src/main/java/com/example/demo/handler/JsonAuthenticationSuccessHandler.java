package com.example.demo.handler;

import com.example.demo.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class JsonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws java.io.IOException {
        response.setContentType("application/json;charset=UTF-8");
        // 客户端会在请求中提供 redirect_uri / client_id 等参数（或前端先调用 /auth/prepare）
        response.getWriter().write(mapper.writeValueAsString(Result.success("登录成功", null)));
    }
}