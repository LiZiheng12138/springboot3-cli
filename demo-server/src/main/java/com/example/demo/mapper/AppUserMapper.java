package com.example.demo.mapper;
import com.example.demo.entity.AppUserEntity;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUserEntity> {

}
