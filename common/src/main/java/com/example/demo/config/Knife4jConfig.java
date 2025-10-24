package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("big-event")//文档的标题
                        .version("1.0")//文档的版本
                        .description("接口文档")//文档的介绍
                        .termsOfService("https://test.com")//网址
                        .contact(new Contact()
                                .name("hjq")
                                .url("https://test.com")
                                .email("test@gamil.com")
                        ));//文档的作者信息，可以使用默认的无关紧要

    }
}