package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class ProductNotInStoreException extends BusinessException {

    public ProductNotInStoreException(Long productId, Long storeId) {
        super(ErrorCode.PRODUCT_NOT_IN_STORE,
            String.format("%s (Product ID : %s, Store ID : %s)",
                ErrorCode.PRODUCT_NOT_IN_STORE.getMessage(), productId, storeId));
    }
}
