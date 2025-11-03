package com.example.demo.config;

import com.example.demo.common.Result;
import com.example.demo.handler.CustomAuthenticationEntryPoint;
import com.example.demo.handler.JsonAuthenticationFailureHandler;
import com.example.demo.handler.JsonAuthenticationSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final JsonAuthenticationSuccessHandler successHandler;
    private final JsonAuthenticationFailureHandler failureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 静态资源放行
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/fonts/**",
                                "/.well-known/**" // ✅ 放行 Chrome 探测请求

                        ).permitAll()
                        // 登录、授权、文档接口放行
                        .requestMatchers(
                                "/auth/**",
                                "/oauth2/token",
                                "/v3/api-docs/**",
                                "/oauth2/jwks",
                                "/swagger-ui/**",
                                "/doc.html"

                ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                // 自定义登录接口
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/login")
                        .successHandler((req, res, auth) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write(new ObjectMapper().writeValueAsString(
                                    Result.success("登录成功")));
                        })
                        .failureHandler((req, res, ex) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.setStatus(200);
                            res.getWriter().write(new ObjectMapper().writeValueAsString(
                                    Result.fail(401, ex.getMessage())));
                        })
                        .permitAll()
                )

                // 核心：异常处理 → 一律返回 JSON
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        return http.build();
    }
}