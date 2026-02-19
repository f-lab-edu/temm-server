package io.github.ktg.temm.domain.dto;

import io.github.ktg.temm.domain.model.Product;
import java.util.List;

public record ProductSearchResult(
    Long id,
    String name,
    ProductSearchStatusResult status,
    String sku,
    String barcode,
    String imageUrl,
    List<ProductSearchCategoryResult> categories
) {
    public static ProductSearchResult from(Product product) {
        return  new ProductSearchResult(
            product.getId(),
            product.getName(),
            new ProductSearchStatusResult(product.getStatus().name(), product.getStatus().getDesc()),
            product.getSku().value(),
            product.getBarcode(),
            product.getImageUrl(),
            product.getCategoryProducts().stream()
                .map(ProductSearchCategoryResult::from)
                .toList()
        );
    }
}
