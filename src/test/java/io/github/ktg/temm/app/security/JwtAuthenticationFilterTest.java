package io.github.ktg.temm.app.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.ktg.temm.domain.provider.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    TokenProvider tokenProvider;

    @Mock
    FilterChain filterChain;

    JwtAuthenticationFilter jwtAuthenticationFilter;
    ObjectMapper objectMapper;
    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenProvider, objectMapper);
        LoginContext.remove();
    }

    @Test
    @DisplayName("Authentication 헤더가 없으면 미인증")
    void notFoundAuthenticationHeaderWillUnauthenticated() throws Exception {
        // given
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // then
        assertThat(LoginContext.get()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("유효한 토큰 정보면 정상 인증 테스트")
    void validAccessTokenWiiAuthenticated() throws Exception {
        // given
        String token = "token1234";
        String authentication = "Bearer " + token;
        String userId = "userId123";
        request.addHeader("Authorization", authentication);
        when(tokenProvider.validateAccessToken(token)).thenReturn(true);
        when(tokenProvider.getUserIdByAccessToken(token)).thenReturn(userId);
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // then
        String loginId = LoginContext.get();
        assertThat(loginId).isEqualTo(userId);
    }

    @Test
    @DisplayName("만료된 토큰 정보면 미인증 테스트")
    void expiredTokenWillUnauthenticated() throws Exception {
        // given
        String token = "token1234";
        String authentication = "Bearer " + token;
        request.addHeader("Authorization", authentication);
        when(tokenProvider.validateAccessToken(token)).thenThrow(ExpiredJwtException.class);
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // then
        assertThat(LoginContext.get()).isNull();
        String content = response.getContentAsString();
        assertThat(content).contains("EXPIRED_ACCESS_TOKEN");

    }

}