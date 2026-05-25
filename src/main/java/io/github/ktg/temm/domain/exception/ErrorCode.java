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
    PRODUCT_NOT_FOUND(ErrorType.ENTITY_NOT_FOUND, "물품을 찾을 수 없습니다."),
    USER_NOT_FOUND(ErrorType.ENTITY_NOT_FOUND, "유저를 찾을 수 없습니다."),
    STORE_NOT_FOUND(ErrorType.ENTITY_NOT_FOUND, "스토어를 찾을 수 없습니다."),
    USER_NOT_IN_STORE(ErrorType.ENTITY_NOT_FOUND, "스토어에 속하지 않은 유저 입니다."),
    LOGIN_REQUIRED(ErrorType.UNAUTHORIZED, "로그인이 필요 합니다."),
    PERMISSION_DENIED(ErrorType.FORBIDDEN, "권한이 없습니다."),
    PLACE_NOT_FOUND(ErrorType.ENTITY_NOT_FOUND, "장소를 찾을 수 없습니다."),
    PLACE_HAS_STOCK(ErrorType.BUSINESS_RULE_VIOLATION, "재고가 남아있어 장소를 삭제할 수 없습니다."),
    INSUFFICIENT_STOCK(ErrorType.BUSINESS_RULE_VIOLATION, "재고가 부족합니다."),
    INVENTORY_NOT_FOUND(ErrorType.ENTITY_NOT_FOUND, "재고를 찾을 수 없습니다."),
    INVENTORY_DUPLICATE(ErrorType.BUSINESS_RULE_VIOLATION, "해당 장소에 이미 등록된 상품입니다."),
    INVENTORY_HAS_STOCK(ErrorType.BUSINESS_RULE_VIOLATION, "재고가 남아있어 삭제할 수 없습니다."),
    PLACE_NOT_IN_SAME_STORE(ErrorType.BUSINESS_RULE_VIOLATION, "두 장소가 같은 스토어에 속하지 않습니다."),
    PRODUCT_NOT_IN_STORE(ErrorType.BUSINESS_RULE_VIOLATION, "해당 상품은 같은 스토어의 상품이 아닙니다.");

    private final ErrorType errorType;
    private final String message;

    ErrorCode(ErrorType errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

}
