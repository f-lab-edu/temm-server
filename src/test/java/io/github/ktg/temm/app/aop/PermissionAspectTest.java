package io.github.ktg.temm.app.aop;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import io.github.ktg.temm.app.exception.PermissionDeniedException;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.security.LoginUser;
import io.github.ktg.temm.app.security.LoginUserStore;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Authorization;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.repository.ProductRepository;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionAspectTest {

    PermissionAspect permissionAspect;

    @Mock
    ProductRepository productRepository;

    @Mock
    JoinPoint joinPoint;

    @Mock
    CheckPermission checkPermission;

    @BeforeEach
    void setUp() {
        permissionAspect = new PermissionAspect(productRepository);
    }

    @AfterEach
    void tearDown() {
        LoginContext.remove();
    }

    public void dummyMethod(Long storeId, Long productId) {}

    public void dummyMethod(DummyRequest request) {}

    record DummyRequest(Long storeId, Long productId) {}

    @Test
    @DisplayName("스토어 ID로 권한 검증 성공")
    void authorizationByStoreId() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginUser loginUser = new LoginUser(userId,
            List.of(new LoginUserStore(storeId, Authorization.MEMBER))
        );
        LoginContext.set(loginUser);

        Method method = this.getClass()
            .getMethod("dummyMethod", Long.class, Long.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);
        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{storeId});

        given(checkPermission.storeId()).willReturn("#storeId");
        given(checkPermission.role()).willReturn(Authorization.MEMBER);

        // when
        // then
        assertThatCode(() -> permissionAspect.checkPermission(joinPoint, checkPermission))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("물품 ID로 권한 검증 성공")
    void authorizationByProductId() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        long productId = 1L;
        LoginUser loginUser = new LoginUser(userId,
            List.of(new LoginUserStore(storeId, Authorization.MEMBER))
        );
        LoginContext.set(loginUser);

        Method method = this.getClass()
            .getMethod("dummyMethod", Long.class, Long.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);
        Product mockProduct = mock(Product.class);

        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{null, productId});
        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct));
        given(mockProduct.getStoreId()).willReturn(storeId);

        given(checkPermission.productId()).willReturn("#productId");
        given(checkPermission.role()).willReturn(Authorization.MEMBER);

        // when
        permissionAspect.checkPermission(joinPoint, checkPermission);

        // then
        then(productRepository).should().findById(productId);
    }


    @Test
    @DisplayName("객체 내 스토어 ID로 권한 검증 성공")
    void authorizationByStoreIdInObject() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginUser loginUser = new LoginUser(userId,
            List.of(new LoginUserStore(storeId, Authorization.MEMBER))
        );
        LoginContext.set(loginUser);

        Method method = this.getClass()
            .getMethod("dummyMethod", DummyRequest.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);

        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{new DummyRequest(storeId, null)});

        given(checkPermission.storeId()).willReturn("#request.storeId");
        given(checkPermission.role()).willReturn(Authorization.MEMBER);

        // when
        // then
        assertThatCode(() -> permissionAspect.checkPermission(joinPoint, checkPermission))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("스토어 매니저 권한 검증 성공")
    void authorizationManager() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginUser loginUser = new LoginUser(userId,
            List.of(new LoginUserStore(storeId, Authorization.MANAGER))
        );
        LoginContext.set(loginUser);

        Method method = this.getClass()
            .getMethod("dummyMethod", DummyRequest.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);

        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{new DummyRequest(storeId, null)});

        given(checkPermission.storeId()).willReturn("#request.storeId");
        given(checkPermission.role()).willReturn(Authorization.MANAGER);

        // when
        // then
        assertThatCode(() -> permissionAspect.checkPermission(joinPoint, checkPermission))
            .doesNotThrowAnyException();
    }


    @Test
    @DisplayName("필요 권한이 멤버지만 매니저일때 성공")
    void authorizationRequiredMemberButManager() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginUser loginUser = new LoginUser(userId,
            List.of(new LoginUserStore(storeId, Authorization.MANAGER))
        );
        LoginContext.set(loginUser);

        Method method = this.getClass()
            .getMethod("dummyMethod", DummyRequest.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);

        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{new DummyRequest(storeId, null)});

        given(checkPermission.storeId()).willReturn("#request.storeId");
        given(checkPermission.role()).willReturn(Authorization.MEMBER);

        // when
        // then
        assertThatCode(() -> permissionAspect.checkPermission(joinPoint, checkPermission))
            .doesNotThrowAnyException();
    }


    @Test
    @DisplayName("스토어 권한 검증 실패 시 예외")
    void authorizationFail() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginUser loginUser = new LoginUser(userId,
            List.of(new LoginUserStore(storeId, Authorization.MEMBER))
        );
        LoginContext.set(loginUser);

        Method method = this.getClass()
            .getMethod("dummyMethod", Long.class, Long.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);
        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{storeId});

        given(checkPermission.storeId()).willReturn("#storeId");
        given(checkPermission.role()).willReturn(Authorization.MANAGER);

        // when
        // then
        assertThatThrownBy(() -> permissionAspect.checkPermission(joinPoint, checkPermission))
            .isInstanceOf(PermissionDeniedException.class)
            .hasMessageContaining(ErrorCode.PERMISSION_DENIED.getMessage());
    }

    @Test
    @DisplayName("어노테이션에 명시한 식별자를 찾지 못하였을 시 예외")
    void authorizationFailByNotStoreIdNotFound() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        long productId = 1L;
        LoginUser loginUser = new LoginUser(userId,
            List.of(new LoginUserStore(storeId, Authorization.MEMBER))
        );
        LoginContext.set(loginUser);

        Method method = this.getClass()
            .getMethod("dummyMethod", Long.class, Long.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);
        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{storeId, productId});

        given(checkPermission.storeId()).willReturn("");
        given(checkPermission.productId()).willReturn("");

        // when
        // then
        assertThatThrownBy(() -> permissionAspect.checkPermission(joinPoint, checkPermission))
            .isInstanceOf(PermissionDeniedException.class)
            .hasMessageContaining(ErrorCode.PERMISSION_DENIED.getMessage());
    }


    @Test
    @DisplayName("유저가 속하지 않은 스토어 요청 시 예외")
    void authorizationFailByUserNotInStore() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        long requestStoreId = 1L;
        long userInStoreId = 2L;
        LoginUser loginUser = new LoginUser(userId,
            List.of(new LoginUserStore(userInStoreId, Authorization.MEMBER))
        );
        LoginContext.set(loginUser);

        Method method = this.getClass()
            .getMethod("dummyMethod", Long.class, Long.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);
        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{requestStoreId, null});

        given(checkPermission.storeId()).willReturn("#storeId");

        // when
        // then
        assertThatThrownBy(() -> permissionAspect.checkPermission(joinPoint, checkPermission))
            .isInstanceOf(PermissionDeniedException.class)
            .hasMessageContaining(ErrorCode.PERMISSION_DENIED.getMessage());
    }


}