package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.ProductRegisterCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProductRegisterRequest(
    @NotNull Long storeId,
    @NotBlank String name,
    @NotBlank String sku,
    String barcode,
    String imageUrl,
    List<Long> categoryIds
) {

    public ProductRegisterCommand toCommand() {
        return new ProductRegisterCommand(
            storeId,
            categoryIds,
            name,
            sku,
            barcode,
            imageUrl
        );
    }
}
