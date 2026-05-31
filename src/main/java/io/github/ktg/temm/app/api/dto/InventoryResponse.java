package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.model.Inventory;

public record InventoryResponse(Long id, Long productId, String productName, int quantity) {

    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(
            inventory.getId(),
            inventory.getProduct().getId(),
            inventory.getProduct().getName(),
            inventory.getQuantity()
        );
    }
}
