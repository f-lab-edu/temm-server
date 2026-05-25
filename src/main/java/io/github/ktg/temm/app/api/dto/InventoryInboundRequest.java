package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.InventoryInboundCommand;
import jakarta.validation.constraints.Min;

public record InventoryInboundRequest(@Min(value = 1, message = "수량은 1 이상이어야 합니다.") int quantity) {

    public InventoryInboundCommand toCommand(Long placeId, Long inventoryId) {
        return new InventoryInboundCommand(placeId, inventoryId, quantity);
    }
}
