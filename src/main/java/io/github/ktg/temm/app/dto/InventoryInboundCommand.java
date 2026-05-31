package io.github.ktg.temm.app.dto;

public record InventoryInboundCommand(Long placeId, Long inventoryId, int quantity) {
}
