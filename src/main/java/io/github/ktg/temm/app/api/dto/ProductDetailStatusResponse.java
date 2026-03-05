package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.dto.ProductDetailStatusResult;

public record ProductDetailStatusResponse(
    String code,
    String desc
) {

    public static ProductDetailStatusResponse from(ProductDetailStatusResult result) {
        return new ProductDetailStatusResponse(result.code(), result.desc());
    }
}
