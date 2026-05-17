package io.github.ktg.temm.domain.exception;

public class PlaceHasStockException extends BusinessException {

    public PlaceHasStockException() {
        super(ErrorCode.PLACE_HAS_STOCK);
    }
}
