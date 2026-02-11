package io.github.ktg.temm.app.infrastructure;

import io.github.ktg.temm.app.exception.SocialLoginFailedException;
import io.github.ktg.temm.app.service.OAuth2TokenResponse;
import io.github.ktg.temm.app.service.Oauth2Properties;
import io.github.ktg.temm.app.service.Oauth2Properties.Provider;
import io.github.ktg.temm.app.service.Oauth2Properties.Registration;
import io.github.ktg.temm.domain.model.SocialType;
import io.github.ktg.temm.domain.provider.SocialTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class RestSocialTokenProvider implements SocialTokenProvider {

    private final Oauth2Properties oauth2Properties;
    private final WebClient webClient;

    @Override
    public String getToken(SocialType socialType, String code) {

        String registrationId = socialType.name().toLowerCase();

        Registration registration = oauth2Properties.getRegistration(registrationId);
        Provider provider = oauth2Properties.getProvider(registrationId);

        MultiValueMap<String, String> parameters = getParameters(registration, code);

        OAuth2TokenResponse response = webClient.post()
            .uri(provider.getTokenUri())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(parameters))
            .retrieve()
            .bodyToMono(OAuth2TokenResponse.class)
            .block();
        if (response == null) {
            throw new SocialLoginFailedException(socialType.name());
        }
        return response.idToken() != null ? response.idToken() : response.accessToken();
    }

    private MultiValueMap<String, String> getParameters(Registration registration, String code) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", registration.getAuthorizationGrantType());
        parameters.add("client_id", registration.getClientId());
        parameters.add("client_secret", registration.getClientSecret());
        parameters.add("redirect_uri", registration.getRedirectUri());
        parameters.add("code", code);
        return parameters;
    }
}
