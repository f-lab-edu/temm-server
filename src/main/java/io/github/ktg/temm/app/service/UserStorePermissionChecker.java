package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.PermissionDeniedException;
import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.domain.model.Authorization;
import io.github.ktg.temm.domain.model.UserStore;
import io.github.ktg.temm.domain.repository.ProductRepository;
import io.github.ktg.temm.domain.repository.UserStoreRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserStorePermissionChecker {

    private final UserStoreRepository userStoreRepository;
    private final ProductRepository productRepository;

    public void checkByStoreId(UUID userId, Long storeId, Authorization requiredRole) {
        UserStore userStore = userStoreRepository.findByUserIdAndStoreId(userId, storeId)
            .orElseThrow(PermissionDeniedException::new);

        if (userStore.getAuthorization().ordinal() < requiredRole.ordinal()) {
            throw new PermissionDeniedException();
        }
    }

    public void checkByProductId(UUID userId, Long productId, Authorization requiredRole) {
        Long storeId = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId))
            .getStoreId();

        checkByStoreId(userId, storeId, requiredRole);
    }

}
