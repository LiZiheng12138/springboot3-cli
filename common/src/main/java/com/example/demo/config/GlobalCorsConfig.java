package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 全局跨域配置
 */
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有域名（生产环境可换成前端域名）
        config.addAllowedOriginPattern("*");

        // 是否允许发送 Cookie
        config.setAllowCredentials(true);

        // 允许的请求头
        config.addAllowedHeader("*");

        // 允许的请求方式
        config.addAllowedMethod("*");

        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);

        // 注册跨域配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // 静态资源跨域（Knife4j/doc.html）
        source.registerCorsConfiguration("/doc.html", config);
        source.registerCorsConfiguration("/swagger-ui.html", config);
        source.registerCorsConfiguration("/v3/api-docs/**", config);
        // OAuth2端点跨域
        source.registerCorsConfiguration("/oauth2/**", config);
        return new CorsFilter(source);
    }
}