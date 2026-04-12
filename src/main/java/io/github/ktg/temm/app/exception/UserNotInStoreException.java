package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import java.util.UUID;

public class UserNotInStoreException extends BusinessException {

    public UserNotInStoreException(Long storeId, UUID userId) {
        super(ErrorCode.USER_NOT_IN_STORE,
            String.format("%s (Store ID: %s, User ID: %s)",
                ErrorCode.USER_NOT_IN_STORE.getMessage(), storeId, userId));
    }
}
