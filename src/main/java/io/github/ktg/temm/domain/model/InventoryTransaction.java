package io.github.ktg.temm.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "inventory_transactions")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "from_place_id")
    private Long fromPlaceId;

    @Column(name = "to_place_id")
    private Long toPlaceId;

    @Column(name = "reason")
    private String reason;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private InventoryTransaction(TransactionType type, Long productId, int quantity,
        Long placeId, Long fromPlaceId, Long toPlaceId, String reason) {
        this.type = type;
        this.productId = productId;
        this.quantity = quantity;
        this.placeId = placeId;
        this.fromPlaceId = fromPlaceId;
        this.toPlaceId = toPlaceId;
        this.reason = reason;
    }

    public static InventoryTransaction inbound(Long productId, Long placeId, int quantity) {
        return new InventoryTransaction(TransactionType.INBOUND, productId, quantity, placeId, null, null, null);
    }

    public static InventoryTransaction outbound(Long productId, Long placeId, int quantity) {
        return new InventoryTransaction(TransactionType.OUTBOUND, productId, quantity, placeId, null, null, null);
    }

    public static InventoryTransaction transfer(Long productId, Long fromPlaceId, Long toPlaceId, int quantity) {
        return new InventoryTransaction(TransactionType.TRANSFER, productId, quantity, null, fromPlaceId, toPlaceId, null);
    }

    public static InventoryTransaction adjustment(Long productId, Long placeId, int newQuantity, String reason) {
        return new InventoryTransaction(TransactionType.ADJUSTMENT, productId, newQuantity, placeId, null, null, reason);
    }
}