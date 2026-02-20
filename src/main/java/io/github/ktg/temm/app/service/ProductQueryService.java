package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.domain.dto.ProductDetailResult;
import io.github.ktg.temm.domain.dto.ProductSearchCondition;
import io.github.ktg.temm.domain.dto.ProductSearchResult;
import io.github.ktg.temm.domain.repository.ProductQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductQueryRepository productQueryRepository;

    public ProductDetailResult getDetail(Long id) {
        return productQueryRepository.findDetailById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Page<ProductSearchResult> search(ProductSearchCondition condition, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return productQueryRepository.search(condition, pageRequest);
    }

}
