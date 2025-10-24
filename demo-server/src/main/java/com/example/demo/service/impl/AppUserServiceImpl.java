package com.example.demo.service.impl;

import com.example.demo.dao.AppUserDao;
import com.example.demo.entity.AppUserEntity;
import com.example.demo.mapper.AppUserMapper;
import com.example.demo.service.AppUserService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDao appUserDao;

    public List<AppUserEntity> findAll() {
        return appUserDao.selectAll();
    }

    public AppUserEntity findByPhone(String phone) {
        return appUserDao.findByPhone(phone);
    }

    public void add(AppUserEntity user) {
        appUserDao.insert(user);
    }
}