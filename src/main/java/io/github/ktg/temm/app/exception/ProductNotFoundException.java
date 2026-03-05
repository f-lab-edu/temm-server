package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(Long productId) {
        super(ErrorCode.PRODUCT_NOT_FOUND,
            String.format("%s (Product ID : %s)", ErrorCode.PRODUCT_NOT_FOUND.getMessage(), productId));
    }
}
