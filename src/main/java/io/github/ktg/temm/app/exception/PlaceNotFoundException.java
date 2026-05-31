package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class PlaceNotFoundException extends BusinessException {

    public PlaceNotFoundException(Long placeId) {
        super(ErrorCode.PLACE_NOT_FOUND,
            String.format("%s (Place ID : %s)", ErrorCode.PLACE_NOT_FOUND.getMessage(), placeId));
    }
}
