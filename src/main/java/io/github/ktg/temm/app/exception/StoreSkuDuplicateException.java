package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class StoreSkuDuplicateException extends BusinessException {

    public StoreSkuDuplicateException(String sku) {
        super(ErrorCode.STORE_SKU_DUPLICATE,
            String.format("%s (SKU : %s)", ErrorCode.STORE_SKU_DUPLICATE.getMessage(), sku));
    }
}
