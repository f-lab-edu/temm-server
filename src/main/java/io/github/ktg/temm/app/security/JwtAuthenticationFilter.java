package io.github.ktg.temm.app.security;

import io.github.ktg.temm.app.api.exception.ErrorResponse;
import io.github.ktg.temm.domain.provider.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response,
        @Nonnull FilterChain filterChain) throws ServletException, IOException {

        // 1. Header 에서 Authentication 추출
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 2. "Bearer " Prefix 제거
        String token = authHeader.substring(BEARER_PREFIX.length());
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 토큰 검증
        try {
            if (tokenProvider.validateAccessToken(token)) {
                // 4. 토큰에서 유저 식별자 조회
                String userId = tokenProvider.getUserIdByAccessToken(token);

                // 5. Login Context 등록
                LoginContext.set(userId);

            }
        } catch (ExpiredJwtException e) {
            writeExpiredTokenResponse(response);
        }
        filterChain.doFilter(request, response);
    }

    private void writeExpiredTokenResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
            objectMapper.writeValueAsString(
                new ErrorResponse(
                    SecurityErrorCode.EXPIRED_ACCESS_TOKEN.name(),
                    SecurityErrorCode.EXPIRED_ACCESS_TOKEN.getMessage())
            )
        );
    }

}
