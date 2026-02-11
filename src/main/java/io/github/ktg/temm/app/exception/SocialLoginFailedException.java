package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class SocialLoginFailedException extends BusinessException {

    public SocialLoginFailedException() {
        super(ErrorCode.SOCIAL_LOGIN_FAILED);
    }

    public SocialLoginFailedException(String message) {
        super(ErrorCode.SOCIAL_LOGIN_FAILED,
            String.format("%s : %s", ErrorCode.SOCIAL_LOGIN_FAILED.getMessage(), message));
    }

}
