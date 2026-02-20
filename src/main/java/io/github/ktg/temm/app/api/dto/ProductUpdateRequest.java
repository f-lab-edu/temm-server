package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.ProductUpdateCommand;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ProductUpdateRequest(
    @NotBlank String name,
    @NotBlank String sku,
    String barcode,
    String imageUrl,
    List<Long> categoryIds
) {

    public ProductUpdateCommand toCommand() {
        return new ProductUpdateCommand(
            name,
            sku,
            barcode,
            imageUrl,
            categoryIds
        );
    }
}
