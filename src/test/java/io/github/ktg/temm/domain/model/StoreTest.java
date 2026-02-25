package io.github.ktg.temm.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StoreTest {

    @Test
    @DisplayName("상점 생성 시 생성자는 매니저 권한")
    void createStoreWithManager() {
        // given
        String storeName = "테스트 스토어";
        User user = mock(User.class);

        // when
        Store store = Store.create(storeName, user);

        // then
        assertThat(store.getName()).isEqualTo(storeName);
        assertThat(store.getUserStores()).hasSize(1);
        assertThat(store.getUserStores().getFirst().getAuthorization()).isEqualTo(Authorization.MANAGER);
        assertThat(store.getUserStores().getFirst().getUser()).isEqualTo(user);
    }
}
