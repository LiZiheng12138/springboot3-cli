package com.example.demo.controller;

import com.example.demo.entity.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuth2AuthorizationService authorizationService;

    @GetMapping("/")
    public Result<?> index(@RequestParam String code) {
        return Result.success(code);
    }

    @PostMapping("/prepare")
    public Result<?> prepare(@RequestParam String client_id,
                             @RequestParam String redirect_uri,
                             @RequestParam(required = false) String scope,
                             @RequestParam(required = false) String state) {
        // 简单返回授权 URL（前端调用后会用 window.location.href 跳转）
        String params = String.format("response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                URLEncoder.encode(client_id, StandardCharsets.UTF_8),
                URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8),
                URLEncoder.encode(scope == null ? "read openid" : scope, StandardCharsets.UTF_8),
                URLEncoder.encode(state == null ? "xyz" : state, StandardCharsets.UTF_8));
        String url = "http://localhost:8788/oauth2/authorize?" + params;
        return Result.success(url);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request, @RequestHeader(value = "Authorization", required = false) String authHeader,
                                 @RequestParam(value = "token", required = false) String tokenValue) {
        if (tokenValue == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenValue = authHeader.substring(7);
        }
        if (tokenValue == null) {
            return Result.fail("缺少 token");
        }
        // 1️⃣ 尝试查找 access_token
        OAuth2Authorization authorization = authorizationService.findByToken(tokenValue, OAuth2TokenType.ACCESS_TOKEN);

        // 2️⃣ 如果没找到 access_token，再查 refresh_token
        if (authorization == null) {
            authorization = authorizationService.findByToken(tokenValue, OAuth2TokenType.REFRESH_TOKEN);
        }

        if (authorization == null) {
            return Result.fail("无效的 token 或已撤销");
        }

        // 3️⃣ 标记 access_token 与 refresh_token 均失效
        OAuth2Authorization.Builder builder = OAuth2Authorization.from(authorization);

        Optional.ofNullable(authorization.getAccessToken()).ifPresent(accessToken ->
                builder.token(accessToken.getToken(),
                        metadata -> metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, true))
        );

        Optional.ofNullable(authorization.getRefreshToken()).ifPresent(refreshToken ->
                builder.token(refreshToken.getToken(),
                        metadata -> metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, true))
        );

        // 4️⃣ 保存更新
        authorizationService.save(builder.build());
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return Result.success("token 已成功撤销");
    }
}