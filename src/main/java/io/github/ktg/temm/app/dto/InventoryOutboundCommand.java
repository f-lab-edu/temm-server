package io.github.ktg.temm.app.dto;

public record InventoryOutboundCommand(Long placeId, Long inventoryId, int quantity) {
}
