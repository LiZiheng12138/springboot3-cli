package com.example.demo.service;
import com.example.demo.entity.AppUser;
import java.util.List;
public interface AppUserService {
    public List<AppUser> findAll();

    public void add(AppUser user);

    public AppUser findByPhone(String phone);
}
