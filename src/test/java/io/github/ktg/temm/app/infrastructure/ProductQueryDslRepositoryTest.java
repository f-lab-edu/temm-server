package io.github.ktg.temm.app.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.ktg.temm.app.config.JpaAuditingConfig;
import io.github.ktg.temm.app.config.QueryDslConfig;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.domain.dto.ProductDetailCategoryResult;
import io.github.ktg.temm.domain.dto.ProductDetailResult;
import io.github.ktg.temm.domain.dto.ProductDetailStatusResult;
import io.github.ktg.temm.domain.model.Category;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.testcontainer.TestContainerForMySQL;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
class ProductQueryDslRepositoryTest extends TestContainerForMySQL {

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    EntityManager em;

    ProductQueryDslRepository productQueryDslRepository;

    @BeforeEach
    void setUp() {
        productQueryDslRepository = new ProductQueryDslRepository(jpaQueryFactory);
        LoginContext.set("testUser");
        Category office = Category.create("사무");
        Category food = Category.create("식품");
        em.persist(office);
        em.persist(food);

        Product product1 = Product.create(
            1L,
            List.of(office, food),
            "AA-BB-12",
            "물품1",
            null,
            null
        );
        Product product2 = Product.create(
            1L,
            null,
            "AA-BB-13",
            "물품2",
            null,
            null
        );
        em.persist(product1);
        em.persist(product2);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("카테고리 있는 물품 상세 조회")
    void findDetailWithCategory() {
        // given
        Long productId = em.createQuery("select p.id from Product p where name = '물품1'",
                Long.class)
            .getSingleResult();

        // when
        Optional<ProductDetailResult> result = productQueryDslRepository.findDetailById(
            productId);

        // then
        assertThat(result).isPresent();
        ProductDetailResult productDetail = result.get();

        assertThat(productDetail.id()).isEqualTo(productId);
        assertThat(productDetail.name()).isEqualTo("물품1");

        List<ProductDetailCategoryResult> categories = productDetail.categories();
        assertThat(categories)
            .hasSize(2)
            .extracting("name")
            .containsExactlyInAnyOrder("사무", "식품");

        ProductDetailStatusResult status = productDetail.status();
        assertThat(status.code()).isEqualTo("REGISTERED");
        assertThat(status.desc()).isEqualTo("등록");
    }

    @Test
    @DisplayName("카테고리 없는 물품 상세 조회")
    void findDetailWithoutCategory() {
        // given
        Long productId = em.createQuery("select p.id from Product p where name = '물품2'",
                Long.class)
            .getSingleResult();

        // when
        Optional<ProductDetailResult> result = productQueryDslRepository.findDetailById(
            productId);

        // then
        assertThat(result).isPresent();
        ProductDetailResult productDetail = result.get();

        assertThat(productDetail.id()).isEqualTo(productId);
        assertThat(productDetail.name()).isEqualTo("물품2");

        List<ProductDetailCategoryResult> categories = productDetail.categories();
        assertThat(categories).hasSize(0);

        ProductDetailStatusResult status = productDetail.status();
        assertThat(status.code()).isEqualTo("REGISTERED");
        assertThat(status.desc()).isEqualTo("등록");
    }



}