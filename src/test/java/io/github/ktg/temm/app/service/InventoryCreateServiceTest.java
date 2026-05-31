package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import io.github.ktg.temm.app.dto.InventoryCreateCommand;
import io.github.ktg.temm.app.exception.InventoryDuplicateException;
import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.app.exception.ProductNotInStoreException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import io.github.ktg.temm.domain.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryCreateServiceTest {

    @Mock
    PlaceRepository placeRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    InventoryRepository inventoryRepository;

    InventoryCreateService inventoryCreateService;

    @BeforeEach
    void setUp() {
        inventoryCreateService = new InventoryCreateService(placeRepository, productRepository, inventoryRepository);
    }

    @Test
    @DisplayName("장소를 찾을 수 없으면 예외")
    void createFailPlaceNotFound() {
        // given
        Long placeId = 1L;
        InventoryCreateCommand command = new InventoryCreateCommand(placeId, 10L);
        given(placeRepository.findById(placeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> inventoryCreateService.create(command))
            .isInstanceOf(PlaceNotFoundException.class)
            .hasMessageContaining(ErrorCode.PLACE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상품을 찾을 수 없으면 예외")
    void createFailProductNotFound() {
        // given
        Long placeId = 1L;
        Long productId = 10L;
        InventoryCreateCommand command = new InventoryCreateCommand(placeId, productId);
        given(placeRepository.findById(placeId)).willReturn(Optional.of(mock(Place.class)));
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> inventoryCreateService.create(command))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("상품이 장소와 다른 스토어에 속하면 예외")
    void createFailProductNotInStore() {
        // given
        Long placeId = 1L;
        Long productId = 10L;
        Long storeId = 1L;
        InventoryCreateCommand command = new InventoryCreateCommand(placeId, productId);

        Store store = mock(Store.class);
        given(store.getId()).willReturn(storeId);
        Place place = mock(Place.class);
        given(place.getStore()).willReturn(store);

        Product product = mock(Product.class);
        given(product.getStoreId()).willReturn(999L);

        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        // then
        assertThatThrownBy(() -> inventoryCreateService.create(command))
            .isInstanceOf(ProductNotInStoreException.class)
            .hasMessageContaining(ErrorCode.PRODUCT_NOT_IN_STORE.getMessage());
    }

    @Test
    @DisplayName("해당 장소에 이미 등록된 상품이면 예외")
    void createFailDuplicate() {
        // given
        Long placeId = 1L;
        Long productId = 10L;
        Long storeId = 1L;
        InventoryCreateCommand command = new InventoryCreateCommand(placeId, productId);

        Store store = mock(Store.class);
        given(store.getId()).willReturn(storeId);
        Place place = mock(Place.class);
        given(place.getStore()).willReturn(store);

        Product product = mock(Product.class);
        given(product.getStoreId()).willReturn(storeId);

        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(inventoryRepository.existsByPlaceIdAndProductId(placeId, productId)).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> inventoryCreateService.create(command))
            .isInstanceOf(InventoryDuplicateException.class)
            .hasMessageContaining(ErrorCode.INVENTORY_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("재고 등록 성공 시 저장")
    void createSuccess() {
        // given
        Long placeId = 1L;
        Long productId = 10L;
        Long storeId = 1L;
        InventoryCreateCommand command = new InventoryCreateCommand(placeId, productId);

        Store store = mock(Store.class);
        given(store.getId()).willReturn(storeId);
        Place place = mock(Place.class);
        given(place.getStore()).willReturn(store);

        Product product = mock(Product.class);
        given(product.getStoreId()).willReturn(storeId);

        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(inventoryRepository.existsByPlaceIdAndProductId(placeId, productId)).willReturn(false);

        // when
        inventoryCreateService.create(command);

        // then
        then(inventoryRepository).should(times(1)).save(any(Inventory.class));
    }
}
