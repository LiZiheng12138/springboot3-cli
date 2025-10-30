package com.example.demo.config;

import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.*;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;

@Component
public class JwkConfig {

    @Bean
    public JwtEncoder jwtEncoder() {
        // 使用对称密钥（HMAC）而不是RSA密钥对
        // 在生产环境中，应该从配置文件中读取密钥
        String secret = "my-secret-key-for-jwt-signing-which-should-be-at-least-32-bytes-long";
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        
        // 构建 JWK
        JWK jwk = new OctetSequenceKey.Builder(secretKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        
        // 构建 JWKSource 并创建 JwtEncoder
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }
}