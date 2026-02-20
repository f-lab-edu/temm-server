package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.dto.ProductSearchCondition;
import io.github.ktg.temm.domain.model.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record ProductSearchRequest(
    @NotNull Long storeId,
    String keyword,
    ProductStatus status
) {

    public ProductSearchCondition toCondition() {
        return new ProductSearchCondition(
            storeId,
            status != null ? status : ProductStatus.REGISTERED,
            keyword
        );
    }
}
