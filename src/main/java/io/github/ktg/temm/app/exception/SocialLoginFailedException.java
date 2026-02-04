package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class SocialLoginFailedException extends BusinessException {

    public SocialLoginFailedException() {
        super(ErrorCode.SOCIAL_LOGIN_FAILED);
    }
}
