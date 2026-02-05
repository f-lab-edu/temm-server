package io.github.ktg.temm.app.security;

import lombok.Getter;

@Getter
public enum SecurityErrorCode {

    EXPIRED_ACCESS_TOKEN("만료된 Access Token 입니다."),
    UNAUTHORIZED("인증이 필요 합니다.");

    private final String message;

    SecurityErrorCode(String message) {
        this.message = message;
    }

}
