package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.InventoryCreateCommand;
import jakarta.validation.constraints.NotNull;

public record InventoryCreateRequest(@NotNull Long productId) {

    public InventoryCreateCommand toCommand(Long placeId) {
        return new InventoryCreateCommand(placeId, productId);
    }
}
