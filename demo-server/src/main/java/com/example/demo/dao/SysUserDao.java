package com.example.demo.dao;

import com.example.demo.entity.SysUser;
import com.example.demo.mapper.SysUserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.demo.entity.table.SysUserTableDef.SYS_USER;

@Component
@RequiredArgsConstructor
public class SysUserDao {
    private final SysUserMapper sysUserMapper;

    public SysUser selectOneByUserId(String userId) {
        return sysUserMapper.selectOneByQuery(
                QueryWrapper.create()
                        .select()
                        .where(SYS_USER.AUTH_USER_ID.eq(userId))
        );
    }

    public void insertUser(SysUser user) {
        sysUserMapper.insert(user);
    }
}
