package io.github.ktg.temm.app.aop;

import static org.springframework.util.StringUtils.hasText;

import io.github.ktg.temm.app.exception.LoginRequiredException;
import io.github.ktg.temm.app.exception.PermissionDeniedException;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.service.UserStorePermissionChecker;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class StorePermissionAspect {

    private final UserStorePermissionChecker userStorePermissionChecker;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();


    @Before("@annotation(checkStorePermission)")
    public void checkPermission(JoinPoint joinPoint, CheckStorePermission checkStorePermission) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        StandardEvaluationContext context = new MethodBasedEvaluationContext(null, method, args, parameterNameDiscoverer);
        UUID userId = getLoginUserId();

        if (hasText(checkStorePermission.storeId())) {
            checkPermissionByStoreId(checkStorePermission, context, userId);
            return;
        }

        if (hasText(checkStorePermission.productId())) {
            checkPermissionByProductId(checkStorePermission, context, userId);
            return;
        }

        if (hasText(checkStorePermission.placeId())) {
            checkPermissionByPlaceId(checkStorePermission, context, userId);
            return;
        }

        throw new PermissionDeniedException();
    }

    private UUID getLoginUserId() {
        try {
            return UUID.fromString(LoginContext.get());
        } catch (NullPointerException e) {
            throw new LoginRequiredException();
        }
    }

    private void checkPermissionByStoreId(CheckStorePermission checkStorePermission,
        StandardEvaluationContext context, UUID userId) {
        Long storeId = getExpression(checkStorePermission.storeId()).getValue(context,
            Long.class);
        userStorePermissionChecker.checkByStoreId(userId, storeId, checkStorePermission.role());
    }

    private void checkPermissionByProductId(CheckStorePermission checkStorePermission,
        StandardEvaluationContext context, UUID userId) {
        Long productId = getExpression(checkStorePermission.productId()).getValue(context,
            Long.class);
        userStorePermissionChecker.checkByProductId(userId, productId, checkStorePermission.role());
    }

    private void checkPermissionByPlaceId(CheckStorePermission checkStorePermission,
        StandardEvaluationContext context, UUID userId) {
        Long placeId = getExpression(checkStorePermission.placeId()).getValue(context, Long.class);
        userStorePermissionChecker.checkByPlaceId(userId, placeId, checkStorePermission.role());
    }

    private Expression getExpression(String expression) {
        return expressionCache.computeIfAbsent(expression, parser::parseExpression);
    }

}
