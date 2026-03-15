package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class PermissionDeniedException extends BusinessException {
    public PermissionDeniedException() {
        super(ErrorCode.PERMISSION_DENIED);
    }
}
