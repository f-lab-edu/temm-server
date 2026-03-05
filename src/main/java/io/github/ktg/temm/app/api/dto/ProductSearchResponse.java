package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.dto.ProductSearchResult;
import java.util.List;

public record ProductSearchResponse(
    Long id,
    String name,
    String sku,
    String barcode,
    String imageUrl,
    ProductSearchStatusResponse status,
    List<ProductSearchCategoryResponse> categories
) {

    public static ProductSearchResponse from(ProductSearchResult result) {
        return new ProductSearchResponse(
            result.id(),
            result.name(),
            result.sku(),
            result.barcode(),
            result.imageUrl(),
            ProductSearchStatusResponse.from(result.status()),
            result.categories().stream()
                .map(ProductSearchCategoryResponse::from)
                .toList()
        );
    }
}
