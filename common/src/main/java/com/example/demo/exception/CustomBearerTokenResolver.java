package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
public class CustomBearerTokenResolver implements BearerTokenResolver {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomBearerTokenResolver.class);
    
    @Override
    public String resolve(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authorization);
        
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            logger.debug("Extracted token length: {}", token.length());
            return token;
        }
        return null;
    }
}
