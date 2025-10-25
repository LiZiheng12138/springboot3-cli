package com.example.demo.dao;

import com.example.demo.entity.AppUser;
import com.example.demo.mapper.AppUserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.demo.entity.table.AppUserTableDef.APP_USER;

@Component
@RequiredArgsConstructor
public class AppUserDao {

    private final AppUserMapper appUserMapper;

    public AppUser selectById(Integer id) {
        return appUserMapper.selectOneById(id);
    }

    public List<AppUser> selectAll() {
        return appUserMapper.selectAll();
    }

    public void insert(AppUser user) {
        appUserMapper.insert(user);
    }

    public void update(AppUser user) {
        appUserMapper.update(user);
    }

    public void delete(Integer id) {
        appUserMapper.deleteById(id);
    }

//    @DataSource("secondary")
    public AppUser findByPhone(String phone) {
        // 添加空值检查
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .where(APP_USER.APP_USER_PHONE.eq(phone));

        // 添加调试日志
        System.out.println("查询手机号: " + phone);
        System.out.println("QueryWrapper: " + queryWrapper.toSQL());

        AppUser account = appUserMapper.selectOneByQuery(queryWrapper);
        System.out.println(account);
        return account;
    }
}