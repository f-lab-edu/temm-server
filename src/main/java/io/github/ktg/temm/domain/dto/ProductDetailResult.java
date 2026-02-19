package io.github.ktg.temm.domain.dto;

import io.github.ktg.temm.domain.model.Product;
import java.util.List;

public record ProductDetailResult(
    Long id,
    String name,
    ProductDetailStatusResult status,
    String sku,
    String barcode,
    String imageUrl,
    List<ProductDetailCategoryResult> categories
) {

    public static ProductDetailResult from(Product product) {
        return new ProductDetailResult(
            product.getId(),
            product.getName(),
            new ProductDetailStatusResult(product.getStatus().name(), product.getStatus().getDesc()),
            product.getSku().value(),
            product.getBarcode(),
            product.getImageUrl(),
            product.getCategoryProducts().stream()
                .map(ProductDetailCategoryResult::from)
                .toList()
            );
    }
}
