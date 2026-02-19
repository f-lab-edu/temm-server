package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.dto.ProductDetailResult;
import java.util.List;

public record ProductDetailResponse(
    Long id,
    String name,
    String sku,
    String barcode,
    String imageUrl,
    ProductDetailStatusResponse status,
    List<ProductDetailCategoryResponse> categories
) {

    public static ProductDetailResponse from(ProductDetailResult result) {
        return new ProductDetailResponse(
            result.id(),
            result.name(),
            result.sku(),
            result.barcode(),
            result.imageUrl(),
            ProductDetailStatusResponse.from(result.status()),
            result.categories().stream().map(ProductDetailCategoryResponse::from).toList()
        );
    }
}
