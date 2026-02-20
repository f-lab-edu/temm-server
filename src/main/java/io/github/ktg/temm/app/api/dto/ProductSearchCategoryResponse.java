package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.dto.ProductSearchCategoryResult;

public record ProductSearchCategoryResponse(
    Long id,
    String name
) {

    public static ProductSearchCategoryResponse from(ProductSearchCategoryResult result) {
        return new ProductSearchCategoryResponse(result.id(), result.name());
    }
}
