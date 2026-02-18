package io.github.ktg.temm.domain.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    EMAIL_IS_REQUIRED(ErrorType.INVALID_INPUT, "이메일은 필수 입니다."),
    EMAIL_PATTERN_NOT_MATCHED(ErrorType.INVALID_INPUT, "이메일 형식이 올바르지 않습니다."),
    SOCIAL_TYPE_NOT_SUPPORTED(ErrorType.INVALID_INPUT, "지원하지 않는 소셜 로그인 입니다."),
    SOCIAL_LOGIN_FAILED(ErrorType.UNAUTHORIZED, "소셜 로그인이 실패 하였습니다."),
    SKU_IS_REQUIRED(ErrorType.INVALID_INPUT, "SKU 정보는 필수 입니다."),
    SKU_PATTERN_NOT_MATCHED(ErrorType.INVALID_INPUT, "SKU 형식이 올바르지 않습니다."),
    STORE_SKU_DUPLICATE(ErrorType.BUSINESS_RULE_VIOLATION, "스토어 내 SKU는 중복 될 수 없습니다"),
    PRODUCT_NOT_FOUND(ErrorType.ENTITY_NOT_FOUND, "물품을 찾을 수 없습니다.");

    private final ErrorType errorType;
    private final String message;

    ErrorCode(ErrorType errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

}
