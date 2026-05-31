package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.InventoryOutboundCommand;
import jakarta.validation.constraints.Min;

public record InventoryOutboundRequest(@Min(value = 1, message = "수량은 1 이상이어야 합니다.") int quantity) {

    public InventoryOutboundCommand toCommand(Long placeId, Long inventoryId) {
        return new InventoryOutboundCommand(placeId, inventoryId, quantity);
    }
}
