package io.github.ktg.temm.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.mock;

import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InventoryTest {

    @Test
    @DisplayName("재고 생성 시 수량은 0")
    void createInventory() {
        // given
        Place place = mock(Place.class);
        Product product = mock(Product.class);

        // when
        Inventory inventory = Inventory.create(place, product);

        // then
        assertThat(inventory.getQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("입고 시 수량 증가")
    void inbound() {
        // given
        Inventory inventory = Inventory.create(mock(Place.class), mock(Product.class));

        // when
        inventory.inbound(10);

        // then
        assertThat(inventory.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("출고 시 수량 감소")
    void outbound() {
        // given
        Inventory inventory = Inventory.create(mock(Place.class), mock(Product.class));
        inventory.inbound(10);

        // when
        inventory.outbound(3);

        // then
        assertThat(inventory.getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("재고보다 많은 수량 출고 시 예외")
    void outboundFailWhenInsufficientStock() {
        // given
        Inventory inventory = Inventory.create(mock(Place.class), mock(Product.class));
        inventory.inbound(5);

        // when
        // then
        assertThatThrownBy(() -> inventory.outbound(10))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining(ErrorCode.INSUFFICIENT_STOCK.getMessage());
    }

    @Test
    @DisplayName("재고 조정")
    void adjust() {
        // given
        Inventory inventory = Inventory.create(mock(Place.class), mock(Product.class));
        inventory.inbound(10);

        // when
        inventory.adjust(3);

        // then
        assertThat(inventory.getQuantity()).isEqualTo(3);
    }
}
