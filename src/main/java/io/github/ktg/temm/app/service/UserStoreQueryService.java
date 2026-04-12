package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.UserStoreQueryResult;
import io.github.ktg.temm.domain.model.UserStore;
import io.github.ktg.temm.domain.repository.UserStoreQueryRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserStoreQueryService {

    private final UserStoreQueryRepository userStoreQueryRepository;

    public List<UserStoreQueryResult> searchByUserId(UUID userId) {
        List<UserStore> result = userStoreQueryRepository.findByUserIdOrderByCreatedAtAsc(
            userId);
        return result.stream()
            .map(UserStoreQueryResult::from)
            .toList();
    }

}
