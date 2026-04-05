package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import io.github.ktg.temm.app.exception.PermissionDeniedException;
import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Authorization;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.UserStore;
import io.github.ktg.temm.domain.repository.ProductRepository;
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
class UserStorePermissionCheckerTest {

    @Mock
    UserStoreRepository userStoreRepository;

    @Mock
    ProductRepository productRepository;

    UserStorePermissionChecker userStorePermissionChecker;

    @BeforeEach
    void setUp() {
        userStorePermissionChecker = new UserStorePermissionChecker(userStoreRepository, productRepository);
    }

    @Test
    @DisplayName("스토어에 속한 유저는 요구 권한과 동일한 권한으로 통과")
    void checkByStoreIdSuccess() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        UserStore userStore = mock(UserStore.class);
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(Optional.of(userStore));
        given(userStore.getAuthorization()).willReturn(Authorization.MEMBER);

        // when
        // then
        assertThatCode(() -> userStorePermissionChecker.checkByStoreId(userId, storeId, Authorization.MEMBER))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("상위 권한 유저는 하위 권한 요구 시 통과")
    void checkByStoreIdHigherRolePasses() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        UserStore userStore = mock(UserStore.class);
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(Optional.of(userStore));
        given(userStore.getAuthorization()).willReturn(Authorization.MANAGER);

        // when
        // then
        assertThatCode(() -> userStorePermissionChecker.checkByStoreId(userId, storeId, Authorization.MEMBER))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("스토어에 속하지 않은 유저는 권한 예외")
    void checkByStoreIdUserNotInStore() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userStorePermissionChecker.checkByStoreId(userId, storeId, Authorization.MEMBER))
            .isInstanceOf(PermissionDeniedException.class)
            .hasMessageContaining(ErrorCode.PERMISSION_DENIED.getMessage());
    }

    @Test
    @DisplayName("요구 권한보다 낮은 권한의 유저는 권한 예외")
    void checkByStoreIdInsufficientRole() {
        // given
        UUID userId = UUID.randomUUID();
        Long storeId = 1L;
        UserStore userStore = mock(UserStore.class);
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(Optional.of(userStore));
        given(userStore.getAuthorization()).willReturn(Authorization.MEMBER);

        // when
        // then
        assertThatThrownBy(() -> userStorePermissionChecker.checkByStoreId(userId, storeId, Authorization.MANAGER))
            .isInstanceOf(PermissionDeniedException.class)
            .hasMessageContaining(ErrorCode.PERMISSION_DENIED.getMessage());
    }

    @Test
    @DisplayName("상품의 스토어에 권한이 있으면 통과")
    void checkByProductIdSuccess() {
        // given
        UUID userId = UUID.randomUUID();
        Long productId = 10L;
        Long storeId = 1L;
        Product product = mock(Product.class);
        UserStore userStore = mock(UserStore.class);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(product.getStoreId()).willReturn(storeId);
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(Optional.of(userStore));
        given(userStore.getAuthorization()).willReturn(Authorization.MANAGER);

        // when
        // then
        assertThatCode(() -> userStorePermissionChecker.checkByProductId(userId, productId, Authorization.MANAGER))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 체크 시 예외")
    void checkByProductIdNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        Long productId = 10L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userStorePermissionChecker.checkByProductId(userId, productId, Authorization.MEMBER))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상품의 스토어에 속하지 않은 유저는 권한 예외")
    void checkByProductIdUserNotInStore() {
        // given
        UUID userId = UUID.randomUUID();
        Long productId = 10L;
        Long storeId = 1L;
        Product product = mock(Product.class);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(product.getStoreId()).willReturn(storeId);
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userStorePermissionChecker.checkByProductId(userId, productId, Authorization.MEMBER))
            .isInstanceOf(PermissionDeniedException.class)
            .hasMessageContaining(ErrorCode.PERMISSION_DENIED.getMessage());
    }

    @Test
    @DisplayName("상품의 스토어에서 요구 권한보다 낮은 권한의 유저는 권한 예외")
    void checkByProductIdInsufficientRole() {
        // given
        UUID userId = UUID.randomUUID();
        Long productId = 10L;
        Long storeId = 1L;
        Product product = mock(Product.class);
        UserStore userStore = mock(UserStore.class);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(product.getStoreId()).willReturn(storeId);
        given(userStoreRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(Optional.of(userStore));
        given(userStore.getAuthorization()).willReturn(Authorization.MEMBER);

        // when
        // then
        assertThatThrownBy(() -> userStorePermissionChecker.checkByProductId(userId, productId, Authorization.MANAGER))
            .isInstanceOf(PermissionDeniedException.class)
            .hasMessageContaining(ErrorCode.PERMISSION_DENIED.getMessage());
    }
}
