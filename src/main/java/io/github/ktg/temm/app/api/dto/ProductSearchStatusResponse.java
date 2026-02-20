package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.dto.ProductSearchStatusResult;

public record ProductSearchStatusResponse(
    String code,
    String desc
) {

    public static ProductSearchStatusResponse from(ProductSearchStatusResult result) {
        return new ProductSearchStatusResponse(result.code(), result.desc());
    }
}
