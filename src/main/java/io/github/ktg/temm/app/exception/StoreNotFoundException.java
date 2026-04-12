package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class StoreNotFoundException extends BusinessException {

    public StoreNotFoundException(Long storeId) {
        super(ErrorCode.STORE_NOT_FOUND,
            String.format("%s (Store ID : %s)", ErrorCode.STORE_NOT_FOUND.getMessage(), storeId));

    }
}
