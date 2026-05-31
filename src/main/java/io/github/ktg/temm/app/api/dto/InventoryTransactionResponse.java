package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.model.InventoryTransaction;
import java.time.LocalDateTime;

public record InventoryTransactionResponse(
    Long id,
    String type,
    Long productId,
    int quantity,
    Long placeId,
    Long fromPlaceId,
    Long toPlaceId,
    String reason,
    String createdBy,
    LocalDateTime createdAt
) {

    public static InventoryTransactionResponse from(InventoryTransaction transaction) {
        return new InventoryTransactionResponse(
            transaction.getId(),
            transaction.getType().name(),
            transaction.getProductId(),
            transaction.getQuantity(),
            transaction.getPlaceId(),
            transaction.getFromPlaceId(),
            transaction.getToPlaceId(),
            transaction.getReason(),
            transaction.getCreatedBy(),
            transaction.getCreatedAt()
        );
    }
}
