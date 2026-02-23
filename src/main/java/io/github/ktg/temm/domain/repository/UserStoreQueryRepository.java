package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.UserStore;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStoreQueryRepository extends JpaRepository<UserStore, Long> {

    @EntityGraph(attributePaths = {"store"})
    List<UserStore> findByUserIdOrderByCreatedAtAsc(UUID userId);

    boolean existsByUserIdAndStoreId(UUID userId, Long storeId);

}
