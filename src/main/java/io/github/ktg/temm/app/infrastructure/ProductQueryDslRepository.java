package io.github.ktg.temm.app.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.ktg.temm.domain.dto.ProductDetailResult;
import io.github.ktg.temm.domain.dto.ProductSearchCondition;
import io.github.ktg.temm.domain.dto.ProductSearchResult;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.QCategory;
import io.github.ktg.temm.domain.model.QCategoryProduct;
import io.github.ktg.temm.domain.model.QProduct;
import io.github.ktg.temm.domain.repository.ProductQueryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryDslRepository implements ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<ProductDetailResult> findDetailById(Long id) {
        QProduct product = QProduct.product;
        QCategoryProduct categoryProduct = QCategoryProduct.categoryProduct;
        QCategory category = QCategory.category;

        Product result = jpaQueryFactory
            .selectFrom(product)
            .leftJoin(product.categoryProducts, categoryProduct).fetchJoin()
            .leftJoin(categoryProduct.category, category).fetchJoin()
            .where(product.id.eq(id)).fetchOne();
        return result == null ? Optional.empty() : Optional.of(ProductDetailResult.from(result));
    }

    @Override
    public Page<ProductSearchResult> search(ProductSearchCondition condition, Pageable pageable) {
        return null;
    }
}
