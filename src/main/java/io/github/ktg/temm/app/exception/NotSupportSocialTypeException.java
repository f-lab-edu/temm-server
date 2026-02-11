package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.SocialType;

public class NotSupportSocialTypeException extends BusinessException {

    public NotSupportSocialTypeException(SocialType socialType) {
        super(ErrorCode.SOCIAL_TYPE_NOT_SUPPORTED,
            String.format("%s (%s)", ErrorCode.SOCIAL_TYPE_NOT_SUPPORTED.getMessage(), socialType.name()));
    }
}
