package com.example.demo.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.demo.dao.SysUserDao;
import com.example.demo.entity.SysUser;
import com.example.demo.auth.BizJwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Tag(name = "系统用户")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {
    private final RestTemplate restTemplate;
    private final JwtDecoder jwtDecoder;
    private final SysUserDao sysUserDao;
    private final BizJwtService bizJwtService;

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
        params.add("redirect_uri", "http://localhost:8789/user/auth/callback");
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

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
            return;
        }
        String authCenterAccessToken = (String) tokenResponse.get("access_token");

        // 3️⃣ 解析认证中心 token，获取 userId
        Jwt jwt = jwtDecoder.decode(authCenterAccessToken);
        String userId = jwt.getClaimAsString("user_id"); // 或 "sub"

        // 4️⃣ 业务系统用户注册 / 查询
        SysUser user = sysUserDao.selectOneByUserId(userId);
        if (user == null) {
            // 自动注册
            user = new SysUser();
            user.setAuthUserId(Integer.valueOf(userId));
            user.setUserName("用户" + userId);
            user.setMenuList(String.join(",", Arrays.asList("USER", "appuser:list"))); // 默认角色
            sysUserDao.insertUser(user);
        }

        // 5️⃣ 根据业务系统权限生成业务系统 token
        String bizAccessToken = bizJwtService.createAccessToken(user);
        String bizRefreshToken = bizJwtService.createRefreshToken(user);
        log.info("用户 {} 登录成功，生成业务系统 token: {}", user.getUserName(), bizAccessToken);

        // 4. 设置 HttpOnly Cookie（推荐）
        Cookie cookie = new Cookie("access_token", bizAccessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 必须
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
        // 5. 设置 刷新令牌 Cookie
        Cookie refreshTokenCookie = new Cookie("refresh_token", bizRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(360000);
        response.addCookie(refreshTokenCookie);
        // 5. 重定向到前端页面
        response.sendRedirect(parse.getString("state"));
    }
}
