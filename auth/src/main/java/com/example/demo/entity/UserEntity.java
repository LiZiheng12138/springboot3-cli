package com.example.demo.entity;

import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "sys_user")
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private String roles;
    private Integer enabled;
}