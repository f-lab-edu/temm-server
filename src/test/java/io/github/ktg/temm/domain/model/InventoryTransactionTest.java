package io.github.ktg.temm.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InventoryTransactionTest {

    @Test
    @DisplayName("입고 이력 생성")
    void inbound() {
        // given
        Long productId = 1L;
        Long placeId = 2L;
        int quantity = 10;

        // when
        InventoryTransaction tx = InventoryTransaction.inbound(productId, placeId, quantity);

        // then
        assertThat(tx.getType()).isEqualTo(TransactionType.INBOUND);
        assertThat(tx.getProductId()).isEqualTo(productId);
        assertThat(tx.getPlaceId()).isEqualTo(placeId);
        assertThat(tx.getQuantity()).isEqualTo(quantity);
        assertThat(tx.getFromPlaceId()).isNull();
        assertThat(tx.getToPlaceId()).isNull();
    }

    @Test
    @DisplayName("출고 이력 생성")
    void outbound() {
        // given
        Long productId = 1L;
        Long placeId = 2L;
        int quantity = 5;

        // when
        InventoryTransaction tx = InventoryTransaction.outbound(productId, placeId, quantity);

        // then
        assertThat(tx.getType()).isEqualTo(TransactionType.OUTBOUND);
        assertThat(tx.getProductId()).isEqualTo(productId);
        assertThat(tx.getPlaceId()).isEqualTo(placeId);
        assertThat(tx.getQuantity()).isEqualTo(quantity);
        assertThat(tx.getFromPlaceId()).isNull();
        assertThat(tx.getToPlaceId()).isNull();
    }

    @Test
    @DisplayName("이전 이력 생성")
    void transfer() {
        // given
        Long productId = 1L;
        Long fromPlaceId = 2L;
        Long toPlaceId = 3L;
        int quantity = 7;

        // when
        InventoryTransaction tx = InventoryTransaction.transfer(productId, fromPlaceId, toPlaceId, quantity);

        // then
        assertThat(tx.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(tx.getProductId()).isEqualTo(productId);
        assertThat(tx.getFromPlaceId()).isEqualTo(fromPlaceId);
        assertThat(tx.getToPlaceId()).isEqualTo(toPlaceId);
        assertThat(tx.getQuantity()).isEqualTo(quantity);
        assertThat(tx.getPlaceId()).isNull();
    }

    @Test
    @DisplayName("조정 이력 생성")
    void adjustment() {
        // given
        Long productId = 1L;
        Long placeId = 2L;
        int newQuantity = 15;
        String reason = "실사 후 조정";

        // when
        InventoryTransaction tx = InventoryTransaction.adjustment(productId, placeId, newQuantity, reason);

        // then
        assertThat(tx.getType()).isEqualTo(TransactionType.ADJUSTMENT);
        assertThat(tx.getProductId()).isEqualTo(productId);
        assertThat(tx.getPlaceId()).isEqualTo(placeId);
        assertThat(tx.getQuantity()).isEqualTo(newQuantity);
        assertThat(tx.getReason()).isEqualTo(reason);
        assertThat(tx.getFromPlaceId()).isNull();
        assertThat(tx.getToPlaceId()).isNull();
    }
}
