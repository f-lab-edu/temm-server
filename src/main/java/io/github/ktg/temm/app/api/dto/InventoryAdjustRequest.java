package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.InventoryAdjustCommand;
import jakarta.validation.constraints.Min;

public record InventoryAdjustRequest(
    @Min(value = 0, message = "수량은 0 이상이어야 합니다.") int newQuantity,
    String reason
) {

    public InventoryAdjustCommand toCommand(Long placeId, Long inventoryId) {
        return new InventoryAdjustCommand(placeId, inventoryId, newQuantity, reason);
    }
}
