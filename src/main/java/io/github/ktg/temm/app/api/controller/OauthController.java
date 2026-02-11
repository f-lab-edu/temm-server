package io.github.ktg.temm.app.api.controller;

import io.github.ktg.temm.app.api.dto.OauthLoginRequest;
import io.github.ktg.temm.app.api.dto.OauthLoginResponse;
import io.github.ktg.temm.app.dto.LoginResult;
import io.github.ktg.temm.app.service.OAuthLoginService;
import io.github.ktg.temm.domain.model.SocialType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class OauthController {

    private final OAuthLoginService oauthLoginService;

    @PostMapping("/{socialType}/login")
    public ResponseEntity<?> login(
            @PathVariable SocialType socialType,
            @RequestBody @Valid OauthLoginRequest oauthLoginRequest) {

        LoginResult loginResult = oauthLoginService.login(socialType,
            oauthLoginRequest.code());
        return ResponseEntity.ok(OauthLoginResponse.from(loginResult));
    }
}
