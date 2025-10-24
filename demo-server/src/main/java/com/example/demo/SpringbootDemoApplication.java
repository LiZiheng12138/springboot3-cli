package com.example.demo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@MapperScan("com.example.demo.mapper")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SpringbootDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootDemoApplication.class, args);
        System.out.println("🚀 Spring Boot 3.0 Demo Started Successfully!");
    }
}
