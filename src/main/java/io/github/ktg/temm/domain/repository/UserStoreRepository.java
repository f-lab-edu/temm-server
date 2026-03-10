package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.model.UserStore;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStoreRepository extends JpaRepository<UserStore, Long> {

    @EntityGraph(attributePaths = {"store"})
    List<UserStore> findByUser(User user);

}
