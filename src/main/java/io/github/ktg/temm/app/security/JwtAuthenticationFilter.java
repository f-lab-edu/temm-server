package io.github.ktg.temm.app.security;

import io.github.ktg.temm.app.api.exception.ErrorResponse;
import io.github.ktg.temm.domain.provider.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN = "accessToken";

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response,
        @Nonnull FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenByHeader(request);
        if (token == null) {
            token = extractTokenByCookie(request);
        }

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (tokenProvider.validateAccessToken(token)) {
                String userId = tokenProvider.getUserIdByAccessToken(token);
                LoginContext.set(userId);
            }
        } catch (ExpiredJwtException e) {
            writeExpiredTokenResponse(response);
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenByCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractTokenByHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (token.isEmpty()) {
            return null;
        }
        return token;
    }

    private void writeExpiredTokenResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
            objectMapper.writeValueAsString(
                new ErrorResponse(
                    SecurityErrorCode.EXPIRED_ACCESS_TOKEN.name(),
                    SecurityErrorCode.EXPIRED_ACCESS_TOKEN.getMessage())
            )
        );
    }

}
