package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    // JWT格式应该是 xxxxx.yyyyy.zzzzz (三个部分由点分隔)
    private static final Pattern JWT_PATTERN = Pattern.compile("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*$");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String[] accessTokenHolder = new String[1];
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    // 只有当token看起来像有效的JWT时才使用它
                    if (token != null && JWT_PATTERN.matcher(token).matches()) {
                        accessTokenHolder[0] = token;
                    }
                    break;
                }
            }
        }

        // 只有当accessTokenHolder[0]不为null时才覆盖Authorization头部
        if (accessTokenHolder[0] != null) {
            final String accessToken = accessTokenHolder[0];
            request = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if ("Authorization".equalsIgnoreCase(name)) {
                        return "Bearer " + accessToken;
                    }
                    return super.getHeader(name);
                }
            };
        }

        filterChain.doFilter(request, response);
    }

    public static void main(String[] args) {
        String str1 = "CxoQIhlmETdYc0BT5qGT5a2i5Lun6Z6g5Li1WF1oElgFVRdeHi02LARuUWtTHjxAXHhJWxFbFFcfIxxAT38HaUNeIEEFCxMSBGUDSBVSTVAcNQE8DyoFaxRLLVwKPxdDL1pSAlY2AkcHalY+Fhk1RVR/RwwXOBRYQANARAsyHzUMbkdrTh4vRA51XE9BWQNLJTUaX1d9EuahhuWsquS7oOmet+S5olR/Rw8ZLQtFCl8Vbg01TGNJJxw+S1NkHAklAFAMVgJOA2tSAE1+HzUJAhxEHiZKHgk9DB8EXQ1ZHT4IN0lgSS9XBSNCERsBFlkfF1AEI0dRGitYIA1TaBAANRcJGWhdEhBKCkcUPUx1STocLU8BaQlEPBFDEAdfFxsjDFMaLEInDUt8AkZgSlhdaAFfDlETQS4yCCwDO0lzGg4jQxR6EFwFSBhPVixGFRMoXDkLBhJaECYNDVNwRVcKSRAbDjIILAM7STQ=";
        String str2 = "CxoQIhlmETdYc0BT5qGT5a2i5Lun6Z6g5Li1WF1oElgFVRdeHi02LARuUWtTHjxAXHhJWxFbFFcfIxxAT38HaUNeIEEFCxMSBGUDSBVSTVAcNQE8DyoFaxRLLVwKPxdDL1pSAlY2AkcHalY+Fhk1RVR/RwwXOBRYQANARAsyHzUMbkdrTh4vRA51XE9BWQNLJTUaX1d9EuahhuWsquS7oOmet+S5olR/Rw8ZLQtFCl8Vbg01TGNJJxw+S1NkHAklAFAMVgJOA2tSAE1+HzUJAhxEHiZKHgk9DB8EXQ1ZHT4IN0lgSS9XBSNCERsBFlkfF1AEI0dRGitYIA1TaBAANRcJGWhdEhBKCkcUPUx1STocLU8BaQlEPBFDEAdfFxsjDFMaLEInDUt8AkZgSlhdaAFfDlETQS4yCCwDO0lzGg4jQxR6EFwFSBhPVixGFRMoXDkLBhJaECYNDVNwRVcKSRAbDjIILAM7STQ=";
        System.out.println(str2.equals(str1)); // true

    }
}