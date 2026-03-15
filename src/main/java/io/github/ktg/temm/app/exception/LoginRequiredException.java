package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class LoginRequiredException extends BusinessException {
    public LoginRequiredException() {
        super(ErrorCode.LOGIN_REQUIRED);
    }
}
