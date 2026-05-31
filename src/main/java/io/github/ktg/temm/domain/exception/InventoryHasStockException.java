package io.github.ktg.temm.domain.exception;

public class InventoryHasStockException extends BusinessException {

    public InventoryHasStockException() {
        super(ErrorCode.INVENTORY_HAS_STOCK);
    }
}
