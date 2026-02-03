package io.github.ktg.temm.domain.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    EMAIL_IS_REQUIRED(ErrorType.INVALID_INPUT, "이메일은 필수 입니다."),
    EMAIL_PATTERN_NOT_MATCHED(ErrorType.INVALID_INPUT, "이메일 형식이 올바르지 않습니다.");

    private final ErrorType errorType;
    private final String message;

    ErrorCode(ErrorType errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

}
