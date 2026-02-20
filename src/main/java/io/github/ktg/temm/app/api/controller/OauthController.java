package io.github.ktg.temm.app.api.controller;

import io.github.ktg.temm.app.api.dto.OauthLoginRequest;
import io.github.ktg.temm.app.api.dto.OauthLoginResponse;
import io.github.ktg.temm.app.dto.LoginResult;
import io.github.ktg.temm.app.service.OAuthLoginService;
import io.github.ktg.temm.domain.model.SocialType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class OauthController {

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    private final OAuthLoginService oauthLoginService;
    private final Long accessTokenValidity;
    private final Long refreshTokenValidity;

    public OauthController(
        OAuthLoginService oauthLoginService,
        @Value("${jwt.access-token-validity}")
        Long accessTokenValidity,
        @Value("${jwt.refresh-token-validity}")
        Long refreshTokenValidity
    ) {
        this.oauthLoginService = oauthLoginService;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    @PostMapping("/{socialType}/login")
    public ResponseEntity<?> login(
            @PathVariable SocialType socialType,
            @RequestBody @Valid OauthLoginRequest oauthLoginRequest) {

        LoginResult loginResult = oauthLoginService.login(socialType,
            oauthLoginRequest.code());
        ResponseCookie accessToken = getHttpOnlyCookie(ACCESS_TOKEN, loginResult.accessToken(),
            accessTokenValidity / 1000);
        ResponseCookie refreshToken = getHttpOnlyCookie(REFRESH_TOKEN, loginResult.refreshToken(),
            refreshTokenValidity / 1000);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessToken.toString(), refreshToken.toString())
            .body(OauthLoginResponse.from(loginResult));
    }

    private ResponseCookie getHttpOnlyCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
            .httpOnly(true)
            .path("/")
            .maxAge(maxAge)
            .build();
    }

}
