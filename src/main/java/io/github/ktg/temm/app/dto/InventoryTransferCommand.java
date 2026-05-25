package io.github.ktg.temm.app.dto;

public record InventoryTransferCommand(Long placeId, Long inventoryId, Long toPlaceId, int quantity) {
}
