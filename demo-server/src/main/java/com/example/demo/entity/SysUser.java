package com.example.demo.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 */
@Data
@Table("sys_user")
public class SysUser {

    private Long id;

    private Integer authUserId;

    private String userName;

    private Integer sex;

    private String menuList;

    private LocalDateTime createTime;

    private String createBy;

    private LocalDateTime updateTime;

    private String updateBy;
}