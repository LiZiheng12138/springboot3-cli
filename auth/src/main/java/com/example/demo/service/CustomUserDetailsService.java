package com.example.demo.service;

import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import static com.example.demo.entity.table.UserEntityTableDef.USER_ENTITY;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity u = userMapper.selectOneByQuery(QueryWrapper.create().select().where(USER_ENTITY.USERNAME.eq(username)));
        if (u == null) throw new UsernameNotFoundException("账号或密码错误");
        try{

            String roles = u.getRoles() == null ? "USER" : u.getRoles();
            return User.withUsername(u.getUsername())
                    .password(u.getPassword())
                    .roles(roles.split(","))
                    .disabled(u.getEnabled() == null || u.getEnabled() == 0)
                    .build();
        } catch (Exception e) {
            throw new UsernameNotFoundException("登录失败");
        }

    }
}