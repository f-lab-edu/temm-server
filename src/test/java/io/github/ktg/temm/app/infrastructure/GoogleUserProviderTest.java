package io.github.ktg.temm.app.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.github.ktg.temm.app.exception.SocialLoginFailedException;
import io.github.ktg.temm.domain.dto.SocialUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleUserProviderTest {

    @Mock
    GoogleIdTokenVerifier verifier;

    GoogleUserProvider googleUserProvider;

    @BeforeEach
    void setUp() {
        googleUserProvider = new GoogleUserProvider(verifier);
    }

    @Test
    @DisplayName("구글 ID 토큰 검증 성공 시 유저 정보를 반환")
    void verifySuccess() throws Exception {
        // given
        String idToken = "idToken";
        String socialId = "socialId";
        String email = "test@test.com";
        String name = "testName";
        GoogleIdToken mockGoogleIdToken = Mockito.mock(GoogleIdToken.class);
        Payload mockPayload = Mockito.mock(Payload.class);
        given(verifier.verify(idToken)).willReturn(mockGoogleIdToken);
        given(mockGoogleIdToken.getPayload()).willReturn(mockPayload);
        given(mockPayload.getSubject()).willReturn(socialId);
        given(mockPayload.getEmail()).willReturn(email);
        given(mockPayload.get("name")).willReturn(name);

        // when
        SocialUserInfo userInfo = googleUserProvider.getUserInfo(idToken);
        // then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.socialId()).isEqualTo(socialId);
        assertThat(userInfo.email()).isEqualTo(email);
        assertThat(userInfo.name()).isEqualTo(name);
    }
    @Test
    @DisplayName("구글 ID 토큰 검증 실패 시 예외 발생")
    void verifyFail() throws Exception {
        // given
        String idToken = "idToken";
        given(verifier.verify(idToken)).willReturn(null);
        // when
        // then
        assertThatThrownBy(() -> googleUserProvider.getUserInfo(idToken))
            .isInstanceOf(SocialLoginFailedException.class);
    }

}