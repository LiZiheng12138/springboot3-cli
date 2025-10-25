package com.example.demo.service.impl;

import com.example.demo.dao.AppUserDao;
import com.example.demo.entity.AppUser;
import com.example.demo.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDao appUserDao;

    public List<AppUser> findAll() {
        return appUserDao.selectAll();
    }

    public AppUser findByPhone(String phone) {
        return appUserDao.findByPhone(phone);
    }

    public void add(AppUser user) {
        appUserDao.insert(user);
    }
}