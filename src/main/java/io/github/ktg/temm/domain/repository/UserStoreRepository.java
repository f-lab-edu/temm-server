package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.model.UserStore;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStoreRepository extends JpaRepository<UserStore, Long> {

    @EntityGraph(attributePaths = {"store"})
    List<UserStore> findByUser(User user);

    Optional<UserStore> findByUserIdAndStoreId(UUID userId, Long storeId);

    boolean existsByUserIdAndStoreId(UUID userId, Long storeId);
}
