package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.*;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class AuthorizationServerInitializer {

    @Bean
    public CommandLineRunner registeredClientInitializer(RegisteredClientRepository repo, PasswordEncoder passwordEncoder) {
        return args -> {
            String clientId = "demo-client";
            if (repo.findByClientId(clientId) == null) {
                RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(clientId)
                        .clientSecret(passwordEncoder.encode("demo-secret"))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://127.0.0.1:8003/")
                        .redirectUri("http://localhost:8003/")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(30))
                                .refreshTokenTimeToLive(Duration.ofDays(30))
                                .reuseRefreshTokens(false) // rotation
                                .build())
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                        .build();

                repo.save(client);
                System.out.println("Registered demo-client");
            } else {
                System.out.println("demo-client already exists");
            }
        };
    }
}