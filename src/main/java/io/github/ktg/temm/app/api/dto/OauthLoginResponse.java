package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.LoginResult;

public record OauthLoginResponse(String accessToken, String refreshToken) {

    public static OauthLoginResponse from(LoginResult loginResult) {
        return new OauthLoginResponse(loginResult.accessToken(), loginResult.refreshToken());
    }

}
