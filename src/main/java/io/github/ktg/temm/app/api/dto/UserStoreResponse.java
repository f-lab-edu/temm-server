package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.UserStoreQueryResult;

public record UserStoreResponse(Long storeId, String storeName) {

    public static UserStoreResponse from(UserStoreQueryResult result) {
        return new UserStoreResponse(result.storeId(), result.storeName());
    }
}