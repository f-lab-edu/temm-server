package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.ProductUpdateCommand;
import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.app.exception.StoreSkuDuplicateException;
import io.github.ktg.temm.domain.model.Category;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.Sku;
import io.github.ktg.temm.domain.repository.CategoryRepository;
import io.github.ktg.temm.domain.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductUpdateService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public void update(Long productId, ProductUpdateCommand command) {
        Product product = getProduct(productId);

        Long storeId = product.getStoreId();
        Sku oldSku = product.getSku();
        Sku newSku = new Sku(command.sku());
        validateStoreSkuDuplicate(storeId, oldSku, newSku);

        product.changeName(command.name());
        product.changeSku(command.sku());
        product.changeBarcode(command.barcode());
        product.changeImage(command.imageUrl());

        List<Category> categories = categoryRepository.findByIdIn(command.categoryIds());
        product.changeCategories(categories);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void validateStoreSkuDuplicate(Long storeId, Sku oldSku, Sku newSku) {
        if (oldSku.equals(newSku)) {
            return;
        }
        if (productRepository.existsByStoreIdAndSku(storeId, newSku)) {
            throw new StoreSkuDuplicateException(newSku.value());
        }
    }
}