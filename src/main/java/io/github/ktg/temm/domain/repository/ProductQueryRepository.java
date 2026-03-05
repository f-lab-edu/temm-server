package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.dto.ProductDetailResult;
import io.github.ktg.temm.domain.dto.ProductSearchCondition;
import io.github.ktg.temm.domain.dto.ProductSearchResult;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {

    Optional<ProductDetailResult> findDetailById(Long id);
    Page<ProductSearchResult> search(ProductSearchCondition condition, Pageable pageable);

}
