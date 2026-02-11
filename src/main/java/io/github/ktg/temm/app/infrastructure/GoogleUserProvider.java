package io.github.ktg.temm.app.infrastructure;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.github.ktg.temm.app.exception.SocialLoginFailedException;
import io.github.ktg.temm.domain.dto.SocialUserInfo;
import io.github.ktg.temm.domain.model.SocialType;
import io.github.ktg.temm.domain.provider.SocialUserProvider;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleUserProvider implements SocialUserProvider {

    private final GoogleIdTokenVerifier verifier;

    @Override
    public SocialType getType() {
        return SocialType.GOOGLE;
    }

    @Override
    public SocialUserInfo getUserInfo(String socialToken) {
        try {
            GoogleIdToken token = verifier.verify(socialToken);
            if (token == null) {
                throw new SocialLoginFailedException();
            }

            Payload payload = token.getPayload();
            return getSocialUserInfoByPayload(payload);
        } catch (GeneralSecurityException | IOException e) {
            throw new SocialLoginFailedException(e.getMessage());
        }
    }

    private SocialUserInfo getSocialUserInfoByPayload(Payload payload) {
        String subject = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        return new SocialUserInfo(subject, email, name);
    }
}
