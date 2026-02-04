package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.SocialType;
import io.github.ktg.temm.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialInfoTypeAndSocialInfoId(SocialType socialType, String socialId);

}
