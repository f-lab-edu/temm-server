package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import java.util.UUID;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(UUID userId) {
        super(ErrorCode.USER_NOT_FOUND,
            String.format("%s (%s)", ErrorCode.USER_NOT_FOUND.getMessage(), userId));
    }
}
