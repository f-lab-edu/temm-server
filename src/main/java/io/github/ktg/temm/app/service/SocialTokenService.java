package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.NotSupportSocialTypeException;
import io.github.ktg.temm.domain.model.SocialType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialTokenService {

    public static final String ID_TOKEN = "id_token";
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RestClientAuthorizationCodeTokenResponseClient tokenExchangeClient = new RestClientAuthorizationCodeTokenResponseClient();

    public String getToken(SocialType socialType, String code) {

        String registrationId = socialType.name().toLowerCase();

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new NotSupportSocialTypeException(socialType);
        }

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
            .clientId(clientRegistration.getClientId())
            .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
            .redirectUri(clientRegistration.getRedirectUri())
            .scopes(clientRegistration.getScopes())
            .attributes(Map.of(OAuth2ParameterNames.REGISTRATION_ID, registrationId))
            .build();

        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.success(code)
            .redirectUri(clientRegistration.getRedirectUri())
            .build();

        OAuth2AuthorizationExchange exchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);

        OAuth2AccessTokenResponse response = tokenExchangeClient.getTokenResponse(
            new OAuth2AuthorizationCodeGrantRequest(clientRegistration, exchange)
        );

        if (response.getAdditionalParameters().containsKey(ID_TOKEN)) {
            return (String) response.getAdditionalParameters().get(ID_TOKEN);
        }

        return response.getAccessToken().getTokenValue();
    }

}
