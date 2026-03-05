package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductLifecycleService {

    private final ProductRepository productRepository;

    public void stop(Long productId) {
        Product product = getProduct(productId);
        product.stop();
    }

    public void delete(Long productId) {
        Product product = getProduct(productId);
        product.delete();
    }

    public void register(Long productId) {
        Product product = getProduct(productId);
        product.register();
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}