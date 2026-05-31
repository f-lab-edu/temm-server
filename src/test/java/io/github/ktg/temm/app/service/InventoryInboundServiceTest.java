package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import io.github.ktg.temm.app.dto.InventoryInboundCommand;
import io.github.ktg.temm.app.exception.InventoryNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.model.InventoryTransaction;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import io.github.ktg.temm.domain.repository.InventoryTransactionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryInboundServiceTest {

    @Mock
    InventoryRepository inventoryRepository;

    @Mock
    InventoryTransactionRepository inventoryTransactionRepository;

    InventoryInboundService inventoryInboundService;

    @BeforeEach
    void setUp() {
        inventoryInboundService = new InventoryInboundService(inventoryRepository, inventoryTransactionRepository);
    }

    @Test
    @DisplayName("재고를 찾을 수 없으면 예외")
    void inboundFailInventoryNotFound() {
        // given
        Long inventoryId = 1L;
        InventoryInboundCommand command = new InventoryInboundCommand(10L, inventoryId, 5);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> inventoryInboundService.inbound(command))
            .isInstanceOf(InventoryNotFoundException.class)
            .hasMessageContaining(ErrorCode.INVENTORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고가 해당 장소에 속하지 않으면 예외")
    void inboundFailInventoryNotInPlace() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        InventoryInboundCommand command = new InventoryInboundCommand(placeId, inventoryId, 5);

        Place place = mock(Place.class);
        given(place.getId()).willReturn(999L);
        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(place);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));

        // when
        // then
        assertThatThrownBy(() -> inventoryInboundService.inbound(command))
            .isInstanceOf(InventoryNotFoundException.class)
            .hasMessageContaining(ErrorCode.INVENTORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("입고 성공 시 수량 증가 및 이력 저장")
    void inboundSuccess() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        Long productId = 20L;
        int quantity = 5;
        InventoryInboundCommand command = new InventoryInboundCommand(placeId, inventoryId, quantity);

        Place place = mock(Place.class);
        given(place.getId()).willReturn(placeId);
        Product product = mock(Product.class);
        given(product.getId()).willReturn(productId);
        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(place);
        given(inventory.getProduct()).willReturn(product);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));

        // when
        inventoryInboundService.inbound(command);

        // then
        then(inventory).should(times(1)).inbound(quantity);
        then(inventoryTransactionRepository).should(times(1)).save(any(InventoryTransaction.class));
    }
}
