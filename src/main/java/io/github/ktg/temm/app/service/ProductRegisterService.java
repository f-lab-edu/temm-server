package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.ProductRegisterCommand;
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
public class ProductRegisterService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public void register(ProductRegisterCommand command) {
        Sku sku = new Sku(command.sku());
        validateStoreSkuDuplicate(command.storeId(), sku);

        List<Long> categoryIds = command.categoryIds();
        List<Category> categories = getCategories(command.storeId(), categoryIds);

        Product newProduct = createProduct(command, categories);
        productRepository.save(newProduct);
    }

    private Product createProduct(ProductRegisterCommand command,
        List<Category> categories) {
        return Product.create(command.storeId(), categories, command.sku(),
            command.productName(), command.barcode(), command.imageUrl());
    }

    private List<Category> getCategories(Long storeId, List<Long> categoryIds) {
        return categoryRepository.findByStoreIdAndIdIn(storeId, categoryIds);
    }

    private void validateStoreSkuDuplicate(Long storeId, Sku sku) {
        if (productRepository.existsByStoreIdAndSku(storeId, sku)) {
            throw new StoreSkuDuplicateException(sku.value());
        }
    }
}
