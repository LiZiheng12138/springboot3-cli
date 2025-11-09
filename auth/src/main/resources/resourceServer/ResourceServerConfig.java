package com.example.demo.auth;

import com.example.demo.handle.SecurityAuthHandlers;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ResourceServerConfig {
    private final BizJwtAuthenticationFilter bizJwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/doc.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/public/**",
                                "/user/auth/callback/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 添加业务系统 token 校验 Filter
                .addFilterBefore(bizJwtAuthenticationFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new SecurityAuthHandlers.JwtAccessDeniedHandler())
                        .authenticationEntryPoint(new SecurityAuthHandlers.JwtAuthenticationEntryPoint())
                );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://127.0.0.1:8788/oauth2/jwks").build();
    }
}