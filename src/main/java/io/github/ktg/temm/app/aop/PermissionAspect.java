package io.github.ktg.temm.app.aop;

import static org.springframework.util.StringUtils.hasText;

import io.github.ktg.temm.app.exception.LoginRequiredException;
import io.github.ktg.temm.app.exception.PermissionDeniedException;
import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.security.LoginUserStore;
import io.github.ktg.temm.domain.model.Authorization;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.repository.ProductRepository;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public class PermissionAspect {

    private final ProductRepository productRepository;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();


    @Before("@annotation(checkPermission)")
    public void checkPermission(JoinPoint joinPoint, CheckPermission checkPermission) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        Long requestStoreId = getRequestStoreId(method, args, checkPermission);

        if (requestStoreId == null) {
            throw new PermissionDeniedException();
        }

        UUID userId = LoginContext.getUserId();
        if (userId == null) {
            throw new LoginRequiredException();
        }
        LoginUserStore targetUserStore = getTargetUserStore(requestStoreId)
            .orElseThrow(PermissionDeniedException::new);

        validateAuthorization(targetUserStore.authorization(), checkPermission.role());
    }

    private Optional<LoginUserStore> getTargetUserStore(Long requestStoreId) {
        List<LoginUserStore> storeInfos = LoginContext.getStoreInfos();
        if (storeInfos == null || storeInfos.isEmpty()) {
            return Optional.empty();
        }
        return storeInfos
            .stream()
            .filter((userStore) -> userStore.storeId().equals(requestStoreId))
            .findAny();
    }

    private Long getRequestStoreId(Method method, Object[] args, CheckPermission checkPermission) {
        StandardEvaluationContext context = new MethodBasedEvaluationContext(null, method, args, parameterNameDiscoverer);

        if (hasText(checkPermission.storeId())) {
            try {
                return getExpression(checkPermission.storeId()).getValue(context, Long.class);
            } catch (Exception ignored) {
            }
        }

        if (!hasText(checkPermission.productId())) {
            return null;
        }
        try {
            Long productId = getExpression(checkPermission.productId()).getValue(context, Long.class);
            if (productId != null) {
                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));
                return product.getStoreId();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private Expression getExpression(String expression) {
        return expressionCache.computeIfAbsent(expression, parser::parseExpression);
    }

    private void validateAuthorization(Authorization userRole, Authorization requiredRole) {
        if (userRole == Authorization.MANAGER) {
            return;
        }
        if (userRole.ordinal() < requiredRole.ordinal()) {
            throw new PermissionDeniedException();
        }
    }

}
