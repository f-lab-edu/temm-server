package io.github.ktg.temm.app.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuth2TokenResponse(
    @JsonProperty("access_token")
    String accessToken,
    @JsonProperty("id_token")
    String idToken
) {

}
