package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import io.github.ktg.temm.app.dto.StoreCreateCommand;
import io.github.ktg.temm.app.exception.UserNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Authorization;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.repository.StoreRepository;
import io.github.ktg.temm.domain.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreCreateServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    StoreRepository storeRepository;

    StoreCreateService storeCreateService;

    @BeforeEach
    void setUp() {
        storeCreateService = new StoreCreateService(userRepository, storeRepository);
    }

    @Test
    @DisplayName("스토어 생성 시 유저가 없으면 예외")
    void createStoreNotFoundUser() {
        // given
        UUID userId = UUID.randomUUID();
        StoreCreateCommand command = new StoreCreateCommand(userId, "스토어 이름");
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> storeCreateService.create(command))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("스토어 생성 시 저장")
    void createStoreSaved() {
        // given
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        StoreCreateCommand command = new StoreCreateCommand(userId, "스토어 이름");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        storeCreateService.create(command);

        // then
        then(storeRepository).should(times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("스토어 생성 시 생성 유저는 관리자 권한 부여")
    void createStoreThenCreateUserIsManager() {
        // given
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        StoreCreateCommand command = new StoreCreateCommand(userId, "스토어 이름");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        storeCreateService.create(command);

        // then
        ArgumentCaptor<Store> captor = ArgumentCaptor.forClass(Store.class);
        then(storeRepository).should(times(1)).save(captor.capture());
        Store store = captor.getValue();
        assertThat(store.getUserStores().size()).isEqualTo(1);
        assertThat(store.getUserStores().getFirst().getAuthorization()).isEqualTo(Authorization.MANAGER);
    }

}