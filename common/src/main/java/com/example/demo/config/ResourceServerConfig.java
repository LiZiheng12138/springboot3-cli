package com.example.demo.config;

import com.example.demo.handle.SecurityAuthHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/doc.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/public/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt()
                        .and()
                        .authenticationEntryPoint(new SecurityAuthHandlers.JwtAuthenticationEntryPoint())
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new SecurityAuthHandlers.JwtAccessDeniedHandler())
                );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // 使用与授权服务器相同的密钥
        String secret = "my-secret-key-for-jwt-signing-which-should-be-at-least-32-bytes-long";
        SecretKey key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}