package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class InventoryNotFoundException extends BusinessException {

    public InventoryNotFoundException(Long inventoryId) {
        super(ErrorCode.INVENTORY_NOT_FOUND,
            String.format("%s (Inventory ID : %s)", ErrorCode.INVENTORY_NOT_FOUND.getMessage(), inventoryId));
    }
}
