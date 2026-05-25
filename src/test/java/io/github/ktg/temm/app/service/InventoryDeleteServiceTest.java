package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import io.github.ktg.temm.app.exception.InventoryNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.exception.InventoryHasStockException;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryDeleteServiceTest {

    @Mock
    InventoryRepository inventoryRepository;

    InventoryDeleteService inventoryDeleteService;

    @BeforeEach
    void setUp() {
        inventoryDeleteService = new InventoryDeleteService(inventoryRepository);
    }

    @Test
    @DisplayName("재고를 찾을 수 없으면 예외")
    void deleteFailInventoryNotFound() {
        // given
        Long placeId = 1L;
        Long inventoryId = 10L;
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> inventoryDeleteService.delete(placeId, inventoryId))
            .isInstanceOf(InventoryNotFoundException.class)
            .hasMessageContaining(ErrorCode.INVENTORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고가 해당 장소에 속하지 않으면 예외")
    void deleteFailInventoryNotInPlace() {
        // given
        Long placeId = 1L;
        Long inventoryId = 10L;

        Place place = mock(Place.class);
        given(place.getId()).willReturn(999L);
        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(place);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));

        // when
        // then
        assertThatThrownBy(() -> inventoryDeleteService.delete(placeId, inventoryId))
            .isInstanceOf(InventoryNotFoundException.class)
            .hasMessageContaining(ErrorCode.INVENTORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고가 남아있으면 삭제 불가 예외")
    void deleteFailHasStock() {
        // given
        Long placeId = 1L;
        Long inventoryId = 10L;

        Place place = mock(Place.class);
        given(place.getId()).willReturn(placeId);
        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(place);
        willThrow(new InventoryHasStockException()).given(inventory).validateDeletable();
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));

        // when
        // then
        assertThatThrownBy(() -> inventoryDeleteService.delete(placeId, inventoryId))
            .isInstanceOf(InventoryHasStockException.class)
            .hasMessageContaining(ErrorCode.INVENTORY_HAS_STOCK.getMessage());
    }

    @Test
    @DisplayName("수량이 0이면 재고 삭제 성공")
    void deleteSuccess() {
        // given
        Long placeId = 1L;
        Long inventoryId = 10L;

        Place place = mock(Place.class);
        given(place.getId()).willReturn(placeId);
        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(place);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));

        // when
        inventoryDeleteService.delete(placeId, inventoryId);

        // then
        then(inventoryRepository).should().delete(inventory);
    }
}
