package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;

import io.github.ktg.temm.app.dto.LoginResult;
import io.github.ktg.temm.app.exception.NotSupportSocialTypeException;
import io.github.ktg.temm.app.exception.SocialLoginFailedException;
import io.github.ktg.temm.domain.dto.SocialUserInfo;
import io.github.ktg.temm.domain.model.SocialType;
import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.provider.SocialUserProvider;
import io.github.ktg.temm.domain.provider.TokenProvider;
import io.github.ktg.temm.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuthLoginServiceTest {

    @Mock
    SocialUserProviderFinder socialUserProviderFinder;

    @Mock
    UserRepository userRepository;

    @Mock
    TokenProvider tokenProvider;

    OAuthLoginService oAuthLoginService;

    @BeforeEach
    void setUp() {
        oAuthLoginService = new OAuthLoginService(socialUserProviderFinder, userRepository,
            tokenProvider);
    }

    @Test
    @DisplayName("지원 하지 않는 소셜 로그인은 예외를 발생")
    void notSupportedSocialType() {
        // given
        SocialType socialType = SocialType.KAKAO;
        String idToken = "idToken";
        given(socialUserProviderFinder.find(socialType)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> oAuthLoginService.login(socialType, idToken))
            .isInstanceOf(NotSupportSocialTypeException.class)
            .hasMessageContaining("KAKAO");
    }

    @Test
    @DisplayName("유효 하지 않은 ID Token 일 경우 예외를 발생")
    void tokenInvalidException() {
        // given
        SocialType socialType = SocialType.KAKAO;
        String idToken = "idToken";
        SocialUserProvider mockSocialUserProvider = mock(SocialUserProvider.class);
        given(socialUserProviderFinder.find(socialType)).willReturn(
            Optional.of(mockSocialUserProvider));
        given(mockSocialUserProvider.getUserInfo(idToken)).willThrow(
            new SocialLoginFailedException()
        );
        // when
        // then
        assertThatThrownBy(() -> oAuthLoginService.login(socialType, idToken))
            .isInstanceOf(SocialLoginFailedException.class);
    }

    @Test
    @DisplayName("기존 유저를 찾지 못하면 가입 처리")
    void notFindUserWillJoin() {
        // given
        SocialType socialType = SocialType.KAKAO;
        String idToken = "idToken";
        String socialId = "socialId";
        String email = "test@test.com";
        String name = "testUser";
        SocialUserInfo socialUserInfo = new SocialUserInfo(socialId, email, name);
        SocialUserProvider mockSocialUserProvider = mock(SocialUserProvider.class);

        given(socialUserProviderFinder.find(socialType)).willReturn(
            Optional.of(mockSocialUserProvider)
        );
        given(mockSocialUserProvider.getUserInfo(idToken)).willReturn(socialUserInfo);
        given(userRepository.findBySocialInfoTypeAndSocialInfoId(socialType, socialId))
            .willReturn(Optional.empty());
        User savedUser = User.create(name, email, socialType, socialId);
        given(userRepository.save(any(User.class)))
            .willReturn(savedUser);
        // when
        oAuthLoginService.login(socialType, idToken);
        // then
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("로그인 후 토큰 정보를 반환")
    void returnTokenInfo() {
        // given
        SocialType socialType = SocialType.KAKAO;
        String idToken = "idToken";
        String socialId = "socialId";
        String email = "test@test.com";
        String name = "testUser";
        SocialUserInfo socialUserInfo = new SocialUserInfo(socialId, email, name);
        User mockUser = User.create(name, email, socialType, socialId);
        SocialUserProvider mockSocialUserProvider = mock(SocialUserProvider.class);

        given(socialUserProviderFinder.find(socialType)).willReturn(
            Optional.of(mockSocialUserProvider)
        );
        given(mockSocialUserProvider.getUserInfo(idToken)).willReturn(socialUserInfo);
        given(userRepository.findBySocialInfoTypeAndSocialInfoId(socialType, socialId))
            .willReturn(Optional.of(mockUser));

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        given(tokenProvider.generateAccessToken(any(String.class))).willReturn(accessToken);
        given(tokenProvider.generateRefreshToken(any(String.class))).willReturn(refreshToken);

        // when
        LoginResult result = oAuthLoginService.login(socialType, idToken);
        // then
        assertThat(result.accessToken()).isEqualTo(accessToken);
        assertThat(result.refreshToken()).isEqualTo(refreshToken);
    }

}