package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.willThrow;

import io.github.ktg.temm.app.dto.InventoryTransferCommand;
import io.github.ktg.temm.app.exception.InventoryNotFoundException;
import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.app.exception.PlaceNotInSameStoreException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.exception.InsufficientStockException;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.model.InventoryTransaction;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import io.github.ktg.temm.domain.repository.InventoryTransactionRepository;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryTransferServiceTest {

    @Mock
    InventoryRepository inventoryRepository;

    @Mock
    InventoryTransactionRepository inventoryTransactionRepository;

    @Mock
    PlaceRepository placeRepository;

    InventoryTransferService inventoryTransferService;

    @BeforeEach
    void setUp() {
        inventoryTransferService = new InventoryTransferService(inventoryRepository, inventoryTransactionRepository, placeRepository);
    }

    @Test
    @DisplayName("재고를 찾을 수 없으면 예외")
    void transferFailInventoryNotFound() {
        // given
        Long inventoryId = 1L;
        InventoryTransferCommand command = new InventoryTransferCommand(10L, inventoryId, 20L, 5);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> inventoryTransferService.transfer(command))
            .isInstanceOf(InventoryNotFoundException.class)
            .hasMessageContaining(ErrorCode.INVENTORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("대상 장소를 찾을 수 없으면 예외")
    void transferFailToPlaceNotFound() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        Long toPlaceId = 20L;
        InventoryTransferCommand command = new InventoryTransferCommand(placeId, inventoryId, toPlaceId, 5);

        Place fromPlace = mock(Place.class);
        given(fromPlace.getId()).willReturn(placeId);
        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(fromPlace);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));
        given(placeRepository.findById(toPlaceId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> inventoryTransferService.transfer(command))
            .isInstanceOf(PlaceNotFoundException.class)
            .hasMessageContaining(ErrorCode.PLACE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이동 출발지와 도착지가 다른 스토어면 예외")
    void transferFailDifferentStore() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        Long toPlaceId = 20L;
        InventoryTransferCommand command = new InventoryTransferCommand(placeId, inventoryId, toPlaceId, 5);

        Store fromStore = mock(Store.class);
        given(fromStore.getId()).willReturn(1L);
        Place fromPlace = mock(Place.class);
        given(fromPlace.getId()).willReturn(placeId);
        given(fromPlace.getStore()).willReturn(fromStore);

        Store toStore = mock(Store.class);
        given(toStore.getId()).willReturn(2L);
        Place toPlace = mock(Place.class);
        given(toPlace.getStore()).willReturn(toStore);

        Inventory inventory = mock(Inventory.class);
        given(inventory.getPlace()).willReturn(fromPlace);
        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(inventory));
        given(placeRepository.findById(toPlaceId)).willReturn(Optional.of(toPlace));

        // when
        // then
        assertThatThrownBy(() -> inventoryTransferService.transfer(command))
            .isInstanceOf(PlaceNotInSameStoreException.class)
            .hasMessageContaining(ErrorCode.PLACE_NOT_IN_SAME_STORE.getMessage());
    }

    @Test
    @DisplayName("재고가 부족하면 예외")
    void transferFailInsufficientStock() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        Long toPlaceId = 20L;
        Long storeId = 1L;
        int quantity = 100;
        InventoryTransferCommand command = new InventoryTransferCommand(placeId, inventoryId, toPlaceId, quantity);

        Store store = mock(Store.class);
        given(store.getId()).willReturn(storeId);

        Place fromPlace = mock(Place.class);
        given(fromPlace.getId()).willReturn(placeId);
        given(fromPlace.getStore()).willReturn(store);

        Place toPlace = mock(Place.class);
        given(toPlace.getStore()).willReturn(store);

        Inventory sourceInventory = mock(Inventory.class);
        given(sourceInventory.getPlace()).willReturn(fromPlace);
        willThrow(new InsufficientStockException()).given(sourceInventory).outbound(quantity);

        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(sourceInventory));
        given(placeRepository.findById(toPlaceId)).willReturn(Optional.of(toPlace));

        // when
        // then
        assertThatThrownBy(() -> inventoryTransferService.transfer(command))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining(ErrorCode.INSUFFICIENT_STOCK.getMessage());
    }

    @Test
    @DisplayName("대상 장소에 재고가 있으면 수량 이전")
    void transferSuccessExistingTargetInventory() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        Long toPlaceId = 20L;
        Long storeId = 1L;
        Long productId = 30L;
        int quantity = 5;
        InventoryTransferCommand command = new InventoryTransferCommand(placeId, inventoryId, toPlaceId, quantity);

        Store store = mock(Store.class);
        given(store.getId()).willReturn(storeId);

        Place fromPlace = mock(Place.class);
        given(fromPlace.getId()).willReturn(placeId);
        given(fromPlace.getStore()).willReturn(store);

        Place toPlace = mock(Place.class);
        given(toPlace.getStore()).willReturn(store);

        Product product = mock(Product.class);
        given(product.getId()).willReturn(productId);

        Inventory sourceInventory = mock(Inventory.class);
        given(sourceInventory.getPlace()).willReturn(fromPlace);
        given(sourceInventory.getProduct()).willReturn(product);

        Inventory targetInventory = mock(Inventory.class);

        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(sourceInventory));
        given(placeRepository.findById(toPlaceId)).willReturn(Optional.of(toPlace));
        given(inventoryRepository.findByPlaceIdAndProductId(toPlaceId, productId))
            .willReturn(Optional.of(targetInventory));

        // when
        inventoryTransferService.transfer(command);

        // then
        then(sourceInventory).should(times(1)).outbound(quantity);
        then(targetInventory).should(times(1)).inbound(quantity);
        then(inventoryTransactionRepository).should(times(1)).save(any(InventoryTransaction.class));
    }

    @Test
    @DisplayName("대상 장소에 재고가 없으면 자동 생성 후 이전")
    void transferSuccessAutoCreateTargetInventory() {
        // given
        Long placeId = 10L;
        Long inventoryId = 1L;
        Long toPlaceId = 20L;
        Long storeId = 1L;
        Long productId = 30L;
        int quantity = 5;
        InventoryTransferCommand command = new InventoryTransferCommand(placeId, inventoryId, toPlaceId, quantity);

        Store store = mock(Store.class);
        given(store.getId()).willReturn(storeId);

        Place fromPlace = mock(Place.class);
        given(fromPlace.getId()).willReturn(placeId);
        given(fromPlace.getStore()).willReturn(store);

        Place toPlace = mock(Place.class);
        given(toPlace.getStore()).willReturn(store);

        Product product = mock(Product.class);
        given(product.getId()).willReturn(productId);

        Inventory sourceInventory = mock(Inventory.class);
        given(sourceInventory.getPlace()).willReturn(fromPlace);
        given(sourceInventory.getProduct()).willReturn(product);

        Inventory newTargetInventory = mock(Inventory.class);

        given(inventoryRepository.findById(inventoryId)).willReturn(Optional.of(sourceInventory));
        given(placeRepository.findById(toPlaceId)).willReturn(Optional.of(toPlace));
        given(inventoryRepository.findByPlaceIdAndProductId(toPlaceId, productId))
            .willReturn(Optional.empty());
        given(inventoryRepository.save(any(Inventory.class))).willReturn(newTargetInventory);

        // when
        inventoryTransferService.transfer(command);

        // then
        then(sourceInventory).should(times(1)).outbound(quantity);
        then(inventoryRepository).should(times(1)).save(any(Inventory.class));
        then(newTargetInventory).should(times(1)).inbound(quantity);
        then(inventoryTransactionRepository).should(times(1)).save(any(InventoryTransaction.class));
    }
}
