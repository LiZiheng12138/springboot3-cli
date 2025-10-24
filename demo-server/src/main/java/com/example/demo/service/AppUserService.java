package com.example.demo.service;
import com.example.demo.entity.AppUserEntity;
import java.util.List;
public interface AppUserService {
    public List<AppUserEntity> findAll();

    public void add(AppUserEntity user);

    public AppUserEntity findByPhone(String phone);
}
