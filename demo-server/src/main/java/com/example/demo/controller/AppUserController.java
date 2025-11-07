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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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

    private final JwtDecoder jwtDecoder;

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
}