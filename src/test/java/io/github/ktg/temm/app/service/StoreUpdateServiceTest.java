package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import io.github.ktg.temm.app.dto.StoreUpdateCommand;
import io.github.ktg.temm.app.exception.StoreNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.repository.StoreRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreUpdateServiceTest {

    StoreUpdateService storeUpdateService;

    @Mock
    StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        storeUpdateService = new StoreUpdateService(storeRepository);
    }

    @Test
    @DisplayName("상점 수정 시 상점이 없으면 예외")
    void updateFailNotFoundStore() {
        // given
        Long storeId = 1L;
        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        StoreUpdateCommand command = new StoreUpdateCommand(null);

        // when
        // then
        assertThatThrownBy(() -> storeUpdateService.update(storeId, command))
            .isInstanceOf(StoreNotFoundException.class)
            .hasMessageContaining(ErrorCode.STORE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상점 이름 변경")
    void updateName() {
        // given
        Long storeId = 1L;
        String oldName = "Old Store Name";
        String newName = "New Store Name";
        Store store = Store.create(oldName, mock(User.class));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        StoreUpdateCommand command = new StoreUpdateCommand(newName);

        // when
        storeUpdateService.update(storeId, command);

        // then
        assertThat(store.getName()).isEqualTo(newName);
    }
}
