package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.domain.dto.ProductDetailResult;
import io.github.ktg.temm.domain.repository.ProductQueryRepository;
import lombok.RequiredArgsConstructor;
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

}
