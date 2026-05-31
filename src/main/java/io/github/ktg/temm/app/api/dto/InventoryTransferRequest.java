package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.InventoryTransferCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryTransferRequest(
    @NotNull Long toPlaceId,
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.") int quantity
) {

    public InventoryTransferCommand toCommand(Long placeId, Long inventoryId) {
        return new InventoryTransferCommand(placeId, inventoryId, toPlaceId, quantity);
    }
}
