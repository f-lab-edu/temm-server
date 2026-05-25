package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.willThrow;

import io.github.ktg.temm.app.dto.InventoryOutboundCommand;
import io.github.ktg.temm.app.exception.InventoryNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.exception.InsufficientStockException;
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
class InventoryOutboundServiceTest {

    @Mock
    InventoryRepository inventoryRepository;

    @Mock
    InventoryTransactionRepository inventoryTransactionRepository;

    InventoryOutboundService inventoryOutboundService;

    @BeforeEach
    void setUp() {
        inventoryOutboundService = new InventoryOutboundService(inventoryRepository, inventoryTransactionRepository);
    }

    @Test
    @DisplayName("재고를 찾을 수 없으면 예외")
    void outboundFailInventoryNotFound() {
        // given
        Long inventoryId = 1L;
        InventoryOutboundCommand command = new InventoryOutboundCommand(10L, inventoryId, 5);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> inventoryOutboundService.outbound(command))
            .isInstanceOf(InventoryNotFoundException.class)
            .hasMessageContaining(ErrorCode.INVENTORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고가 해당 장소에 속하지 않으면 예외")
    void outboundFailInventoryNotInPlace() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        InventoryOutboundCommand command = new InventoryOutboundCommand(placeId, inventoryId, 5);

        Place place = mock(Place.class);
        given(place.getId()).willReturn(999L);
        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(place);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));

        // when
        // then
        assertThatThrownBy(() -> inventoryOutboundService.outbound(command))
            .isInstanceOf(InventoryNotFoundException.class)
            .hasMessageContaining(ErrorCode.INVENTORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고가 부족하면 예외")
    void outboundFailInsufficientStock() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        int quantity = 100;
        InventoryOutboundCommand command = new InventoryOutboundCommand(placeId, inventoryId, quantity);

        Place place = mock(Place.class);
        given(place.getId()).willReturn(placeId);
        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(place);
        willThrow(new InsufficientStockException()).given(inventory).outbound(quantity);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));

        // when
        // then
        assertThatThrownBy(() -> inventoryOutboundService.outbound(command))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining(ErrorCode.INSUFFICIENT_STOCK.getMessage());
    }

    @Test
    @DisplayName("출고 성공 시 수량 감소 및 이력 저장")
    void outboundSuccess() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        Long productId = 20L;
        int quantity = 3;
        InventoryOutboundCommand command = new InventoryOutboundCommand(placeId, inventoryId, quantity);

        Place place = mock(Place.class);
        given(place.getId()).willReturn(placeId);
        Product product = mock(Product.class);
        given(product.getId()).willReturn(productId);
        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(place);
        given(inventory.getProduct()).willReturn(product);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));

        // when
        inventoryOutboundService.outbound(command);

        // then
        then(inventory).should(times(1)).outbound(quantity);
        then(inventoryTransactionRepository).should(times(1)).save(any(InventoryTransaction.class));
    }
}
