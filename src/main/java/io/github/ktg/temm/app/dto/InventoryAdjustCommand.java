package io.github.ktg.temm.app.dto;

public record InventoryAdjustCommand(Long placeId, Long inventoryId, int newQuantity, String reason) {
}
