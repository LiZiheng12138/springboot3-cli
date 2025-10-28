package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Slf4j
public class Knife4jConfig {
    @Value("${server.port}")
    private String port;

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("接口文档地址：http://localhost:" +  port + "/doc.html");
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