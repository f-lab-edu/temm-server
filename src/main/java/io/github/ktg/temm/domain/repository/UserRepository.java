package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
