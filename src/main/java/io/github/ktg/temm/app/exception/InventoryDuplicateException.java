package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class InventoryDuplicateException extends BusinessException {

    public InventoryDuplicateException(Long placeId, Long productId) {
        super(ErrorCode.INVENTORY_DUPLICATE,
            String.format("%s (Place ID : %s, Product ID : %s)",
                ErrorCode.INVENTORY_DUPLICATE.getMessage(), placeId, productId));
    }
}
