package io.github.ktg.temm.app.dto;

import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.model.UserStore;

public record UserStoreQueryResult(Long storeId, String storeName) {

    public static UserStoreQueryResult from(UserStore userStore) {
        Store store = userStore.getStore();
        return new UserStoreQueryResult(store.getId(), store.getName());
    }

}
