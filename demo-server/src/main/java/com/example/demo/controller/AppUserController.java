package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.AppUser;
import com.example.demo.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/appUser")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @Operation(summary = "获取所有App用户列表")
    @GetMapping("/list")
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