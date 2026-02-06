package io.github.ktg.temm.app.service;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public class Oauth2Properties {

    private Map<String, Registration> registration;
    private Map<String, Provider> provider;

    public Registration getRegistration(String registrationId) {
        return registration.get(registrationId);
    }

    public Provider getProvider(String providerId) {
        return provider.get(providerId);
    }

    @Getter
    @Setter
    public static class Registration {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String authorizationGrantType;
        private String scope;
    }

    @Getter
    @Setter
    public static class Provider {
        private String tokenUri;
    }
}
