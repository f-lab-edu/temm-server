package io.github.ktg.temm.app.api.dto;

import jakarta.validation.constraints.NotBlank;

public record OauthLoginRequest(
    @NotBlank(message = "Authorization Code 는 필수 입니다.")
    String code
) {

}
