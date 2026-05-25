package io.github.ktg.temm.app.exception;

import io.github.ktg.temm.domain.exception.BusinessException;
import io.github.ktg.temm.domain.exception.ErrorCode;

public class PlaceNotInSameStoreException extends BusinessException {

    public PlaceNotInSameStoreException(Long fromPlaceId, Long toPlaceId) {
        super(ErrorCode.PLACE_NOT_IN_SAME_STORE,
            String.format("%s (From Place ID : %s, To Place ID : %s)",
                ErrorCode.PLACE_NOT_IN_SAME_STORE.getMessage(), fromPlaceId, toPlaceId));
    }
}
