package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import io.github.ktg.temm.app.exception.UserNotInStoreException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.model.UserStore;
import io.github.ktg.temm.domain.repository.StoreRepository;
import io.github.ktg.temm.domain.repository.UserRepository;
import io.github.ktg.temm.domain.repository.UserStoreRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreMemberServiceTest {

    StoreMemberService storeMemberService;

    @Mock
    UserRepository userRepository;

    @Mock
    StoreRepository storeRepository;

    @Mock
    UserStoreRepository userStoreRepository;

    @BeforeEach
    void setUp() {
        storeMemberService = new StoreMemberService(userRepository, storeRepository, userStoreRepository);
    }

    @Test
    @DisplayName("멤버 추가 성공")
    void addMember() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        User mockUser = mock(User.class);
        Store mockStore = mock(Store.class);
        given(userStoreRepository.existsByUserIdAndStoreId(userId, storeId)).willReturn(false);
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(storeRepository.findById(storeId)).willReturn(Optional.of(mockStore));

        // when
        storeMemberService.addMember(storeId, userId);

        // then
        then(userStoreRepository).should(times(1)).save(any(UserStore.class));
    }

    @Test
    @DisplayName("이미 존재하는 멤버 추가 시 무시")
    void addExistingMember() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        given(userStoreRepository.existsByUserIdAndStoreId(userId, storeId)).willReturn(true);

        // when
        storeMemberService.addMember(storeId, userId);

        // then
        then(userStoreRepository).should(times(0)).save(any(UserStore.class));
    }

    @Test
    @DisplayName("멤버 삭제")
    void removeMember() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        UserStore mockUserStore = mock(UserStore.class);

        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(
            Optional.of(mockUserStore));

        // when
        storeMemberService.removeMember(storeId, userId);

        // then
        then(userStoreRepository).should(times(1)).delete(mockUserStore);
    }

    @Test
    @DisplayName("존재하지 않는 멤버 삭제 시 예외")
    void removeNonExistingMember() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(
            Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> storeMemberService.removeMember(storeId, userId))
            .isInstanceOf(UserNotInStoreException.class)
            .hasMessageContaining(ErrorCode.USER_NOT_IN_STORE.getMessage());
    }

    @Test
    @DisplayName("매니저 권한 부여")
    void grantManagerRole() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        UserStore mockUserStore = mock(UserStore.class);
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(
            Optional.of(mockUserStore));

        // when
        storeMemberService.grantManagerRole(storeId, userId);

        // then
        then(mockUserStore).should(times(1)).toManager();
    }

    @Test
    @DisplayName("멤버 권한 부여")
    void grantMemberRole() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        UserStore mockUserStore = mock(UserStore.class);
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(
            Optional.of(mockUserStore));

        // when
        storeMemberService.grantMemberRole(storeId, userId);

        // then
        then(mockUserStore).should(times(1)).toMember();
    }

    @Test
    @DisplayName("스토어에 속하지 않는 유저 권한 부여 시 예외")
    void grantMemberFailUserNotInStore() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(
            Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> storeMemberService.grantMemberRole(storeId, userId))
            .isInstanceOf(UserNotInStoreException.class)
            .hasMessageContaining(ErrorCode.USER_NOT_IN_STORE.getMessage());
    }
}
