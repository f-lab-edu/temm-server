package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ktg.temm.app.config.JpaAuditingConfig;
import io.github.ktg.temm.app.config.QueryDslConfig;
import io.github.ktg.temm.app.dto.UserStoreQueryResult;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.security.LoginUser;
import io.github.ktg.temm.domain.model.SocialType;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.repository.UserStoreQueryRepository;
import io.github.ktg.temm.testcontainer.TestContainerForMySQL;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
class UserStoreQueryServiceTest extends TestContainerForMySQL {

    @Autowired
    UserStoreQueryRepository userStoreQueryRepository;

    @Autowired
    EntityManager em;

    UserStoreQueryService userStoreQueryService;

    @BeforeEach
    void setUp() {
        userStoreQueryService = new UserStoreQueryService(userStoreQueryRepository);
        User user = User.create("testuser", "testuser@naver.com", SocialType.GOOGLE, "aaaa");
        em.persist(user);
        LoginContext.set(new LoginUser(user.getId(), List.of()));

        Store store1 = Store.create("store1", user);
        Store store2 = Store.create("store2", user);

        em.persist(store1);
        em.persist(store2);

        em.flush();
        em.clear();

    }

    @Test
    @DisplayName("유저 아이디로 스토어 검색")
    void searchByUserId() {
        // given
        UUID userId = em.createQuery("select u.id from User u where name = 'testuser'",
                UUID.class)
            .getSingleResult();

        // when
        List<UserStoreQueryResult> result = userStoreQueryService.searchByUserId(
            userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("storeName")
            .containsExactlyInAnyOrder("store1", "store2");
    }


}