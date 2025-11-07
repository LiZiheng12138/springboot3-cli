package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
@MapperScan("com.example.demo.mapper")
@SpringBootApplication
public class AuthServer {
    public static void main(String[] args) {
        SpringApplication.run(AuthServer.class, args);
        System.out.println("🚀 Auth Server Started Successfully!");
    }

    // 临时在启动类中加一段
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void encodePasswords() {
        String encoded = passwordEncoder.encode("123456");
        System.out.println("BCrypt encoded password: " + encoded);
    }
}