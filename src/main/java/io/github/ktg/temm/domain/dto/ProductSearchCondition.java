package io.github.ktg.temm.domain.dto;

import io.github.ktg.temm.domain.model.ProductStatus;

public record ProductSearchCondition(
    Long storeId,
    ProductStatus status,
    String keyword
) {

}
