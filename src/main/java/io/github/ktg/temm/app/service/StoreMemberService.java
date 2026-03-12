package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.StoreNotFoundException;
import io.github.ktg.temm.app.exception.UserNotFoundException;
import io.github.ktg.temm.app.exception.UserNotInStoreException;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.model.UserStore;
import io.github.ktg.temm.domain.repository.StoreRepository;
import io.github.ktg.temm.domain.repository.UserRepository;
import io.github.ktg.temm.domain.repository.UserStoreRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreMemberService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final UserStoreRepository userStoreRepository;

    public void addMember(Long storeId, UUID userId) {
        if (userStoreRepository.existsByUserIdAndStoreId(userId, storeId)) {
            return;
        }

        User user = getUser(userId);
        Store store = getStore(storeId);

        UserStore userStore = UserStore.createMember(store, user);
        userStoreRepository.save(userStore);
    }

    public void removeMember(Long storeId, UUID userId) {
        UserStore userStore = getUserStore(storeId, userId);
        userStoreRepository.delete(userStore);
    }

    public void grantManagerRole(Long storeId, UUID userId) {
        UserStore userStore = getUserStore(storeId, userId);
        userStore.toManager();
    }

    public void grantMemberRole(Long storeId, UUID userId) {
        UserStore userStore = getUserStore(storeId, userId);
        userStore.toMember();
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> new StoreNotFoundException(storeId));
    }

    private UserStore getUserStore(Long storeId, UUID userId) {
        return userStoreRepository.findByUserIdAndStoreId(userId, storeId)
            .orElseThrow(() -> new UserNotInStoreException(storeId, userId));
    }
}
