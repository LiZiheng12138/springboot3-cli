package com.example.demo.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.demo.common.Result;
import com.example.demo.entity.AppUser;
import com.example.demo.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/appUser")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @Operation(summary = "获取所有App用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<List<AppUser>> list() {
        return Result.success(appUserService.findAll());
    }

    @Operation(summary = "根据手机号查询用户信息")
    @GetMapping("/{phone}")
    public Result<AppUser> getByPhone(@PathVariable String phone) {
        return Result.success(appUserService.findByPhone(phone));
    }

    @Operation(summary = "新增App用户")
    @PostMapping("/add")
    public String add(@RequestBody AppUser user) {
        appUserService.add(user);
        return "success";
    }

    private final RestTemplate restTemplate;

    @GetMapping("/auth/callback")
    public void authCallback(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IOException {
        // 1. 用 code 换取 token
        // 1. 构造请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //uri base64编码解码
        // 1️⃣ 使用URL安全的Base64解码器
        // 2️⃣ URL decode（对应 encodeURIComponent）
        JSONObject parse = JSONObject.parseObject(state);
        // 2. 构造请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:8789/appUser/auth/callback");
        params.add("client_id", parse.getString("clientId"));
        params.add("client_secret", parse.getString("clientSecret"));
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        // 3. 调用授权服务器 /oauth2/token
        String tokenUrl = "http://localhost:8788/oauth2/token";
        ResponseEntity<Map> res = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );
        // 4. 返回 access_token / id_token
        Map<String, Object> tokenResponse = res.getBody();

        // 4. 设置 HttpOnly Cookie（推荐）
        Cookie cookie = new Cookie("access_token", (String) tokenResponse.get("access_token"));
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 必须
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
        // 5. 设置 刷新令牌 Cookie
        Cookie refreshTokenCookie = new Cookie("refresh_token", (String) tokenResponse.get("refresh_token"));
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(3600);
        response.addCookie(refreshTokenCookie);
        // 5. 重定向到前端页面
        response.sendRedirect(parse.getString("state"));
    }
}