package io.github.ktg.temm.app.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ktg.temm.domain.provider.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    @Test
    @DisplayName("유저 ID 로 Access Token 생성")
    void createAccessTokenByUserId() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L, 10000L);
        String userId = "user";
        // when
        String accessToken = tokenProvider.generateAccessToken(userId);
        // then
        assertThat(accessToken).isNotBlank();
    }

    @Test
    @DisplayName("Access Token 검증 성공")
    void validateAccessToken() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L, 10000L);
        String userId = "user";
        String accessToken = tokenProvider.generateAccessToken(userId);
        // when
        boolean validateAccessToken = tokenProvider.validateAccessToken(accessToken);
        // then
        assertThat(validateAccessToken).isTrue();
    }

    @Test
    @DisplayName("Access Token 검증 실패")
    void invalidAccessToken() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L, 10000L);
        String accessToken = "token!";
        // when
        boolean validateAccessToken = tokenProvider.validateAccessToken(accessToken);
        // then
        assertThat(validateAccessToken).isFalse();
    }

    @Test
    @DisplayName("토큰 만료 시 예외 발생")
    void expiredToken() {
        // given
        TokenProvider tokenProvider = getTokenProvider(1L, 10000L);
        String userId = "user";
        String accessToken = tokenProvider.generateAccessToken(userId);
        // when
        // then
        assertThatThrownBy(() -> tokenProvider.validateAccessToken(accessToken))
            .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("토큰 내 유저 식별자 조회")
    void getUserIdByToken() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L, 10000L);
        String userId = "user";
        String accessToken = tokenProvider.generateAccessToken(userId);
        // when
        String userIdByAccessToken = tokenProvider.getUserIdByAccessToken(accessToken);
        // then
        assertThat(userIdByAccessToken).isEqualTo(userId);
    }

    TokenProvider getTokenProvider(Long accessTokenValidity, Long refreshTokenValidity) {
        return new JwtTokenProvider(
            "issuer",
            accessTokenValidity,
            refreshTokenValidity,
            "dGlja2V0aW5nLXNlcnZpY2UtYXV0aGVudGljYXRpb24="
        );
    }

}