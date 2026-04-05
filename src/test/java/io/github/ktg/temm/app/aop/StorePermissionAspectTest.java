package io.github.ktg.temm.app.aop;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import io.github.ktg.temm.app.exception.PermissionDeniedException;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.service.UserStorePermissionChecker;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Authorization;
import java.lang.reflect.Method;
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
class StorePermissionAspectTest {

    StorePermissionAspect storePermissionAspect;

    @Mock
    UserStorePermissionChecker userStorePermissionChecker;

    @Mock
    JoinPoint joinPoint;

    @Mock
    CheckStorePermission checkStorePermission;

    @BeforeEach
    void setUp() {
        storePermissionAspect = new StorePermissionAspect(userStorePermissionChecker);
    }

    @AfterEach
    void tearDown() {
        LoginContext.remove();
    }

    public void dummyMethod(Long storeId, Long productId) {}

    public void dummyMethod(DummyRequest request) {}

    record DummyRequest(Long storeId, Long productId) {}

    @Test
    @DisplayName("스토어 ID가 있으면 스토어ID로 권한 검사")
    void authorizationByStoreId() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginContext.set(userId.toString());

        Method method = this.getClass()
            .getMethod("dummyMethod", Long.class, Long.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);
        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{storeId});

        given(checkStorePermission.storeId()).willReturn("#storeId");
        given(checkStorePermission.role()).willReturn(Authorization.MEMBER);

        // when
        // then
        assertThatCode(() -> storePermissionAspect.checkPermission(joinPoint, checkStorePermission))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("스토어 ID가 없으면 물품 ID로 권한 검사")
    void authorizationByProductId() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long productId = 1L;
        LoginContext.set(userId.toString());

        Method method = this.getClass()
            .getMethod("dummyMethod", Long.class, Long.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);

        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{null, productId});

        given(checkStorePermission.productId()).willReturn("#productId");
        given(checkStorePermission.role()).willReturn(Authorization.MEMBER);


        // when
        storePermissionAspect.checkPermission(joinPoint, checkStorePermission);

        // then
        then(userStorePermissionChecker).should().checkByProductId(userId, productId, Authorization.MEMBER);
    }


    @Test
    @DisplayName("객체 내 스토어 ID로 권한 검사")
    void authorizationByStoreIdInObject() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginContext.set(userId.toString());

        Method method = this.getClass()
            .getMethod("dummyMethod", DummyRequest.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);

        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{new DummyRequest(storeId, null)});

        given(checkStorePermission.storeId()).willReturn("#request.storeId");
        given(checkStorePermission.role()).willReturn(Authorization.MEMBER);

        // when
        // then
        assertThatCode(() -> storePermissionAspect.checkPermission(joinPoint, checkStorePermission))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("스토어 ID, 물품 ID가 없으면 예외")
    void authorizationManager() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginContext.set(userId.toString());

        Method method = this.getClass()
            .getMethod("dummyMethod", DummyRequest.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);

        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{new DummyRequest(storeId, null)});

        given(checkStorePermission.storeId()).willReturn("#request.storeId");
        given(checkStorePermission.role()).willReturn(Authorization.MANAGER);

        // when
        // then
        assertThatCode(() -> storePermissionAspect.checkPermission(joinPoint, checkStorePermission))
            .doesNotThrowAnyException();
    }


    @Test
    @DisplayName("필요 권한이 멤버지만 매니저일때 성공")
    void authorizationRequiredMemberButManager() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginContext.set(userId.toString());

        Method method = this.getClass()
            .getMethod("dummyMethod", DummyRequest.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);

        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{new DummyRequest(storeId, null)});

        given(checkStorePermission.storeId()).willReturn("#request.storeId");
        given(checkStorePermission.role()).willReturn(Authorization.MEMBER);

        // when
        // then
        assertThatCode(() -> storePermissionAspect.checkPermission(joinPoint, checkStorePermission))
            .doesNotThrowAnyException();
    }


    @Test
    @DisplayName("스토어 권한 검증 실패 시 예외")
    void authorizationFail() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        long storeId = 1L;
        LoginContext.set(userId.toString());

        Method method = this.getClass()
            .getMethod("dummyMethod", Long.class, Long.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);
        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{storeId});

        given(checkStorePermission.storeId()).willReturn("#storeId");
        given(checkStorePermission.role()).willReturn(Authorization.MANAGER);
        willThrow(new PermissionDeniedException())
            .given(userStorePermissionChecker)
            .checkByStoreId(userId, storeId, Authorization.MANAGER);

        // when
        // then
        assertThatThrownBy(() -> storePermissionAspect.checkPermission(joinPoint, checkStorePermission))
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
        LoginContext.set(userId.toString());

        Method method = this.getClass()
            .getMethod("dummyMethod", Long.class, Long.class);

        MethodSignature mockMethodSignature = mock(MethodSignature.class);
        given(joinPoint.getSignature()).willReturn(mockMethodSignature);
        given(mockMethodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{storeId, productId});

        given(checkStorePermission.storeId()).willReturn("");
        given(checkStorePermission.productId()).willReturn("");

        // when
        // then
        assertThatThrownBy(() -> storePermissionAspect.checkPermission(joinPoint, checkStorePermission))
            .isInstanceOf(PermissionDeniedException.class)
            .hasMessageContaining(ErrorCode.PERMISSION_DENIED.getMessage());
    }


}