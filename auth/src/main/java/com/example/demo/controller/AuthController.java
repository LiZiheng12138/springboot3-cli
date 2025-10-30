package com.example.demo.controller;

import com.example.demo.common.Result;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/auth")
public class AuthController {

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
}