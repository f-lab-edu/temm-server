package io.github.ktg.temm.app.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.mock;

import io.github.ktg.temm.domain.model.UserStore;
import io.github.ktg.temm.domain.provider.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    @Test
    @DisplayName("유저 ID 로 Access Token 생성")
    void createAccessTokenByUserId() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L);
        String userId = "user";
        // when
        String accessToken = tokenProvider.generateAccessToken(userId, List.of());
        // then
        assertThat(accessToken).isNotBlank();
    }
    
    @Test
    @DisplayName("Access Token 생성 시 유저 - 스토어 정보 클레임에 저장")
    void createAccessTokenWithUserStoresClaims() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L);
        String userId = "user";
        List<UserStore> userStores = List.of(
            mock(UserStore.class), mock(UserStore.class)
        );
        // when
        String accessToken = tokenProvider.generateAccessToken(userId, userStores);
        // then
        List<UserStore> result = tokenProvider.getUserStoresByAccessToken(
            accessToken);
        assertThat(result).hasSize(2);

    }

    @Test
    @DisplayName("Access Token 검증 성공")
    void validateAccessToken() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L);
        String userId = "user";
        String accessToken = tokenProvider.generateAccessToken(userId, List.of());
        // when
        boolean validateAccessToken = tokenProvider.validateAccessToken(accessToken);
        // then
        assertThat(validateAccessToken).isTrue();
    }

    @Test
    @DisplayName("Access Token 검증 실패")
    void invalidAccessToken() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L);
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
        TokenProvider tokenProvider = getTokenProvider(1L);
        String userId = "user";
        String accessToken = tokenProvider.generateAccessToken(userId, List.of());
        // when
        // then
        assertThatThrownBy(() -> tokenProvider.validateAccessToken(accessToken))
            .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("토큰 내 유저 식별자 조회")
    void getUserIdByToken() {
        // given
        TokenProvider tokenProvider = getTokenProvider(2000L);
        String userId = "user";
        String accessToken = tokenProvider.generateAccessToken(userId, List.of());
        // when
        String userIdByAccessToken = tokenProvider.getUserIdByAccessToken(accessToken);
        // then
        assertThat(userIdByAccessToken).isEqualTo(userId);
    }

    TokenProvider getTokenProvider(Long accessTokenValidity) {
        return new JwtTokenProvider(
            "issuer",
            accessTokenValidity,
            10000L,
            "dGlja2V0aW5nLXNlcnZpY2UtYXV0aGVudGljYXRpb24="
        );
    }

}