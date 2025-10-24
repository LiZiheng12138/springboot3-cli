package com.example.demo.dao;

import com.example.demo.aop.DataSource;
import com.example.demo.entity.AppUserEntity;
import com.example.demo.entity.table.AppUserTableDef;
import com.example.demo.mapper.AppUserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AppUserDao {

    private final AppUserMapper appUserMapper;

    public AppUserEntity selectById(Integer id) {
        return appUserMapper.selectOneById(id);
    }

    public List<AppUserEntity> selectAll() {
        return appUserMapper.selectAll();
    }

    public void insert(AppUserEntity user) {
        appUserMapper.insert(user);
    }

    public void update(AppUserEntity user) {
        appUserMapper.update(user);
    }

    public void delete(Integer id) {
        appUserMapper.deleteById(id);
    }

    @DataSource("secondary")
    public AppUserEntity findByPhone(String phone) {
        return appUserMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where(AppUserTableDef.APP_USER.AppUserPhone.eq(phone))
        );
    }
}