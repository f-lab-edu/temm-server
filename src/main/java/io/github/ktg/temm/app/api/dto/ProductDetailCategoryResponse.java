package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.dto.ProductDetailCategoryResult;

public record ProductDetailCategoryResponse(
    Long id,
    String name
) {

    public static ProductDetailCategoryResponse from(ProductDetailCategoryResult result) {
        return new ProductDetailCategoryResponse(result.id(), result.name());
    }
}
