package io.github.ktg.temm.app.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.ktg.temm.app.config.JpaAuditingConfig;
import io.github.ktg.temm.app.config.QueryDslConfig;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.domain.dto.ProductDetailCategoryResult;
import io.github.ktg.temm.domain.dto.ProductDetailResult;
import io.github.ktg.temm.domain.dto.ProductDetailStatusResult;
import io.github.ktg.temm.domain.dto.ProductSearchCondition;
import io.github.ktg.temm.domain.dto.ProductSearchResult;
import io.github.ktg.temm.domain.model.Category;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.ProductStatus;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
        Category office = Category.create(1L, "사무");
        Category food = Category.create(1L, "식품");
        Category daily = Category.create(1L, "생필품");
        Category electronics = Category.create(1L, "가전");
        Category furniture = Category.create(1L, "가구");
        em.persist(office);
        em.persist(food);
        em.persist(daily);
        em.persist(electronics);
        em.persist(furniture);

        Product product1 = createProduct(1L, List.of(office, food), "AA-BB-12", "물품1", null);
        Product product2 = createProduct(1L, null, "AA-BB-13", "물품2", null);

        Product product2_1 = createProduct(2L, List.of(office, food, daily, electronics, furniture),
            "CC-DD-12", "물품2-1", null);
        Product product2_2 = createProduct(2L, List.of(office), "CC-DD-13", "물품2-2", null);
        Product product2_3 = createProduct(2L, List.of(office), "CC-DD-14", "물품2-3", null);
        Product product2_4 = createProduct(2L, List.of(office, food), "CC-DD-15", "물품2-4", null
        );
        Product product2_5 = createProduct(2L, null, "CC-DD-16", "물품2-5", "11111111111");

        em.persist(product1);
        em.persist(product2);

        em.persist(product2_1);
        em.persist(product2_2);
        em.persist(product2_3);
        em.persist(product2_4);
        em.persist(product2_5);
        em.flush();
        em.clear();
    }

    private Product createProduct(long storeId, List<Category> categories,
        String sku, String name, String barcode) {
        return Product.create(
            storeId,
            categories,
            sku,
            name,
            barcode,
            null
        );
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

    @Test
    @DisplayName("물품 스토어로 검색")
    void searchWithStoreId() {
        // given
        Long storeId = 2L;
        ProductSearchCondition condition = new ProductSearchCondition(storeId, null,
            null);
        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        Page<ProductSearchResult> result = productQueryDslRepository.search(condition, pageRequest);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(5);
    }

    @Test
    @DisplayName("물품 스토어, 키워드로 검색")
    void searchWithStoreIdAndKeyword() {
        // given
        Long storeId = 2L;
        ProductSearchCondition condition = new ProductSearchCondition(storeId, null, "물품2-1");
        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        Page<ProductSearchResult> result = productQueryDslRepository.search(condition, pageRequest);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("물품2-1");
    }

    @Test
    @DisplayName("물품 스토어, 상태로 검색")
    void searchWithStoreIdAndStatus() {
        // given
        Long storeId = 2L;
        Product product = em.createQuery("select p from Product p where name = '물품2-1'",
                Product.class)
            .getSingleResult();
        product.stop();
        em.flush();
        em.clear();

        ProductSearchCondition condition = new ProductSearchCondition(storeId,
            ProductStatus.STOPPED, null);
        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        Page<ProductSearchResult> result = productQueryDslRepository.search(condition, pageRequest);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("물품2-1");
    }

    @Test
    @DisplayName("물품 스토어, 키워드, 상태로 검색")
    void searchWithStoreIdAndKeywordAndStatus() {
        // given
        Long storeId = 2L;
        Product product = em.createQuery("select p from Product p where name = '물품2-1'",
                Product.class)
            .getSingleResult();
        product.stop();
        em.flush();
        em.clear();

        ProductSearchCondition condition = new ProductSearchCondition(storeId,
            ProductStatus.STOPPED, "물품2-1");
        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        Page<ProductSearchResult> result = productQueryDslRepository.search(condition, pageRequest);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("물품2-1");
    }

    @Test
    @DisplayName("물품 스토어, 바코드로 검색")
    void searchWithStoreIdAndBarcode() {
        // given
        Long storeId = 2L;
        String barcode = "11111111111";

        ProductSearchCondition condition = new ProductSearchCondition(storeId, null, barcode);
        PageRequest pageRequest = PageRequest.of(0, 10);
        // when
        Page<ProductSearchResult> result = productQueryDslRepository.search(condition, pageRequest);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("물품 검색 페이징 테스트")
    void searchWithPaging() {
        // given
        Long storeId = 2L;
        ProductSearchCondition condition = new ProductSearchCondition(storeId, null,
            null);
        PageRequest onePageRequest = PageRequest.of(0, 3);
        PageRequest twoPageRequest = PageRequest.of(1, 3);
        // when
        Page<ProductSearchResult> onePage = productQueryDslRepository.search(condition, onePageRequest);
        Page<ProductSearchResult> twoPage = productQueryDslRepository.search(condition, twoPageRequest);
        // then
        assertThat(onePage).isNotNull();
        assertThat(onePage.getContent()).hasSize(3);
        assertThat(twoPage).isNotNull();
        assertThat(twoPage.getContent()).hasSize(2);
    }

}
