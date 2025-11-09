package com.example.demo.auth;

import com.example.demo.entity.SysUser;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 服务(自定义的token生成与刷新 需要生成密钥证书或改成配置文件读取key)
 * keystore 执行命令
 * # 生成 keystore（会交互输入密码/信息）
 *  keytool -genkeypair \
 *   -alias jwt \
 *   -keyalg RSA \
 *   -keysize 2048 \
 *   -keystore jwt.jks \
 *   -validity 3650
 *
 * # 导出公钥证书（X.509），然后把它转换为 PEM（可选）
 *  keytool -exportcert -alias jwt -keystore jwt.jks -rfc -file jwt-public.pem
 *
 *  密码需要在配置文件中配置
 *
 *  注：如果不需要业务系统自行处理认证授权逻辑，无需该类
 */

//@Slf4j
//@Service
public class BizJwtService {

    // 对称加密密钥（可以用更安全的方式管理）
// 从配置文件注入 keystore 信息
    @Value("${jwt.keystore.path}")
    private Resource keystoreResource;

    @Value("${jwt.keystore.store-password}")
    private String keystorePassword;

    @Value("${jwt.keystore.key-alias}")
    private String keyAlias;

    @Value("${jwt.keystore.key-password}")
    private String keyPassword;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    // token 有效时间
    private final long accessTokenValidity = 24 * 3600_000;       // 1小时
    private final long refreshTokenValidity = 7 * 24 * 3600_000L; // 7天

    @PostConstruct
    public void init() throws Exception {
        // 加载 JKS，并提取 RSA 私钥/公钥
        try (InputStream is = keystoreResource.getInputStream()) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(is, keystorePassword.toCharArray());

            Key key = keyStore.getKey(keyAlias, keyPassword.toCharArray());
            if (!(key instanceof PrivateKey)) {
                throw new IllegalStateException("Keystore does not contain a private key for alias: " + keyAlias);
            }
            Certificate cert = keyStore.getCertificate(keyAlias);
            if (cert == null) {
                throw new IllegalStateException("No certificate found in keystore for alias: " + keyAlias);
            }

            privateKey = (RSAPrivateKey) key;
            publicKey = (RSAPublicKey) cert.getPublicKey();
        }
    }



    /** 生成 access_token（生产） */
    public String createAccessToken(SysUser user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUserName())
                .claim("roles", user.getMenuList()) // 逗号分隔
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenValidity))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    /** 生成 refresh_token（生产） */
    public String createRefreshToken(SysUser user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenValidity))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    /** 解析并返回 Claims（若无效或过期则抛异常或返回 null，见下方方法） */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            // 上层可以根据具体异常类型进一步判断
            return null;
        }
    }

    /** 检查 token 状态（区分过期与无效） */
    public TokenStatus checkTokenStatus(String token) {
        if (token == null) return TokenStatus.INVALID;
        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    // 允许 5 秒时钟偏差 避免运行期间的token过期
                    .setAllowedClockSkewSeconds(-5)
                    .build()
                    .parseClaimsJws(token);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return TokenStatus.INVALID;
        }
    }

    /** 刷新 access token（基于有效的 refreshToken） */
    public String refreshAccessToken(String refreshToken, SysUser user) {
        Claims c = parseToken(refreshToken);
        if (c == null) return null;
        return createAccessToken(user);
    }

    /** 刷新 refresh token（延长有效期） */
    public String refreshRefreshToken(String refreshToken, SysUser user) {
        Claims c = parseToken(refreshToken);
        if (c == null) return null;
        return createRefreshToken(user);
    }

    // 状态枚举
    public enum TokenStatus {
        VALID, EXPIRED, INVALID
    }

    public UserDetails getUserDetailsFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;

        String username = claims.get("username", String.class);
        String roles = claims.get("roles", String.class); // 逗号分隔

        Collection<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(String::trim)
                .map(role -> (GrantedAuthority) () -> role) // Lambda 实现 GrantedAuthority
                .collect(Collectors.toList());

        return User.withUsername(username)
                .password("") // token 不需要密码
                .authorities(authorities) // 这里传 Collection<GrantedAuthority>
                .build();
    }

    public static void main(String[] args) {
        BizJwtService service = new BizJwtService();
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUserName("admin");
        user.setMenuList("ROLE_ADMIN,ROLE_USER");
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiLnlKjmiLcxIiwicm9sZXMiOiJVU0VSLGFwcHVzZXI6bGlzdCIsImlhdCI6MTc2MjUxNDU0MSwiZXhwIjoxNzYyNTE0NTQ2fQ.-pLtoomP8YiLeJu9FIyl9Fw0h1drLOg2kuMqpNkGCkA";
        String refreshToken = service.createRefreshToken(user);
        for(int i = 0; i < 60; i++) {
            TokenStatus tokenStatus = service.checkTokenStatus(accessToken);
            if (tokenStatus.equals(TokenStatus.EXPIRED)) {
                System.out.println("accessToken 已过期，尝试刷新...");
//                accessToken = service.refreshAccessToken(refreshToken, user);
//                System.out.println("刷新后的 accessToken: " + accessToken);
            } else {
                System.out.println("accessToken 有效");
            }
            System.out.println();
            try {
                Thread.sleep(1000);
            }catch ( InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}