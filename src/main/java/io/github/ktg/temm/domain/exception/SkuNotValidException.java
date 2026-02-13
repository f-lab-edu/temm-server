package io.github.ktg.temm.domain.exception;

public class SkuNotValidException extends BusinessException {

    public SkuNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
