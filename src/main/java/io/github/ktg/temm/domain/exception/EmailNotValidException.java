package io.github.ktg.temm.domain.exception;

public class EmailNotValidException extends BusinessException {

    public EmailNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EmailNotValidException(ErrorCode errorCode, String email) {
        super(errorCode,
            String.format("%s (%s)", errorCode.getMessage(), email));
    }
}
