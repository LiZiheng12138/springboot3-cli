package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

/**
 * 资源服务器配置
 * 复制到需要认证的资源服务中，@Component注释需要添加
 */
//@Component
//@Slf4j
//public class CustomBearerTokenResolver implements BearerTokenResolver {
//
//    @Override
//    public String resolve(HttpServletRequest request) {
//        String authorization = request.getHeader("Authorization");
//        log.info("请求url:{}, token:{}", request.getRequestURI(),authorization);
//
//        if (authorization != null && authorization.startsWith("Bearer ")) {
//            return authorization.substring(7);
//        }
//        return null;
//    }
//}
