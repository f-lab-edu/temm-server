package io.github.ktg.temm.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.ktg.temm.app.config.JpaAuditingConfig;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.testcontainer.TestContainerForMySQL;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class BaseEntityTest extends TestContainerForMySQL {

    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        LoginContext.set("loginUserId");
    }

    @Test
    @DisplayName("JPA Auditing 생성일, 생성자 테스트")
    void auditingCreatedAtCreatedBy() {
        // given
        TestEntity testEntity = new TestEntity("name");
        // when
        entityManager.persist(testEntity);
        // then
        assertThat(testEntity.getCreatedAt()).isNotNull();
        assertThat(testEntity.getCreatedBy()).isNotNull();
    }

    @Test
    @DisplayName("JPA Auditing 수정일, 수정자 테스트")
    void auditingUpdatedAtUpdatedBy() {
        // given
        TestEntity testEntity = new TestEntity("name1");
        // when
        entityManager.persist(testEntity);

        TestEntity findEntity = entityManager.find(TestEntity.class, testEntity.getId());
        findEntity.changeName("name2");
        entityManager.flush();
        entityManager.clear();
        // then
        assertThat(findEntity.getUpdatedAt()).isNotNull();
        assertThat(findEntity.getUpdatedBy()).isNotNull();
        assertThat(findEntity.getUpdatedAt()).isAfter(findEntity.getCreatedAt());
    }

}

@Entity
class TestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public TestEntity() {
    }

    TestEntity(String name) {
        this.name = name;
    }

    void changeName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

}