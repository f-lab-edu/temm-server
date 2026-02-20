package io.github.ktg.temm.app.infrastructure;

import static io.github.ktg.temm.domain.model.QCategory.category;
import static io.github.ktg.temm.domain.model.QCategoryProduct.categoryProduct;
import static io.github.ktg.temm.domain.model.QProduct.product;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.ktg.temm.domain.dto.ProductDetailResult;
import io.github.ktg.temm.domain.dto.ProductSearchCondition;
import io.github.ktg.temm.domain.dto.ProductSearchResult;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.ProductStatus;
import io.github.ktg.temm.domain.repository.ProductQueryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryDslRepository implements ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<ProductDetailResult> findDetailById(Long id) {
        Product result = jpaQueryFactory
            .selectFrom(product)
            .leftJoin(product.categoryProducts, categoryProduct).fetchJoin()
            .leftJoin(categoryProduct.category, category).fetchJoin()
            .where(product.id.eq(id))
            .fetchOne();
        return result == null ? Optional.empty() : Optional.of(ProductDetailResult.from(result));
    }

    @Override
    public Page<ProductSearchResult> search(ProductSearchCondition condition, Pageable pageable) {
        List<Product> result = jpaQueryFactory
            .selectFrom(product)
            .distinct()
            .leftJoin(product.categoryProducts, categoryProduct)
            .leftJoin(categoryProduct.category, category)
            .where(
                storeIdEq(condition.storeId()),
                statusEq(condition.status()),
                keywordIsLikeOrEq(condition.keyword())
            )
            .orderBy(product.name.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(product.countDistinct())
            .from(product)
            .leftJoin(product.categoryProducts, categoryProduct)
            .leftJoin(categoryProduct.category, category)
            .where(
                storeIdEq(condition.storeId()),
                statusEq(condition.status()),
                keywordIsLikeOrEq(condition.keyword())
            );

        List<ProductSearchResult> contents = result.stream()
            .map(ProductSearchResult::from)
            .toList();
        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    private BooleanExpression storeIdEq(Long storeId) {
        return storeId == null ? null : product.storeId.eq(storeId);
    }

    private BooleanExpression statusEq(ProductStatus status) {
        return status == null ? null : product.status.eq(status);
    }

    private BooleanExpression keywordIsLikeOrEq(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return product.name.contains(keyword)
            .or(product.sku.value.contains(keyword))
            .or(category.name.contains(keyword))
            .or(product.barcode.eq(keyword));
    }

}
