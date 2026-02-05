package io.github.ktg.temm.app.api.exception;

import lombok.Getter;

@Getter
public enum GlobalErrorCode {
    INVALID_INPUT("잘못된 입력 값 입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생 했습니다.");

    private final String message;

    GlobalErrorCode(String message) {
        this.message = message;
    }
}
