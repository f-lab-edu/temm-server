package io.github.ktg.temm.app.security;

import io.github.ktg.temm.domain.model.Authorization;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.model.UserStore;

public record LoginUserStore(Long storeId, Authorization authorization) {

    public static LoginUserStore from(UserStore userStore) {
        Store store = userStore.getStore();
        return new LoginUserStore(store.getId(), userStore.getAuthorization());
    }

}
