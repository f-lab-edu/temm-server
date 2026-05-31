package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryQueryServiceTest {

    @Mock
    PlaceRepository placeRepository;

    @Mock
    InventoryRepository inventoryRepository;

    InventoryQueryService inventoryQueryService;

    @BeforeEach
    void setUp() {
        inventoryQueryService = new InventoryQueryService(placeRepository, inventoryRepository);
    }

    @Test
    @DisplayName("장소를 찾을 수 없으면 예외")
    void findByPlaceIdFailPlaceNotFound() {
        // given
        Long placeId = 1L;
        given(placeRepository.findById(placeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> inventoryQueryService.findByPlaceId(placeId))
            .isInstanceOf(PlaceNotFoundException.class)
            .hasMessageContaining(ErrorCode.PLACE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장소별 재고 목록 조회")
    void findByPlaceId() {
        // given
        Long placeId = 1L;
        List<Inventory> inventories = List.of(mock(Inventory.class), mock(Inventory.class));
        given(placeRepository.findById(placeId)).willReturn(Optional.of(mock(Place.class)));
        given(inventoryRepository.findByPlaceIdWithProduct(placeId)).willReturn(inventories);

        // when
        List<Inventory> result = inventoryQueryService.findByPlaceId(placeId);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("재고가 없으면 빈 목록 반환")
    void findByPlaceIdEmpty() {
        // given
        Long placeId = 1L;
        given(placeRepository.findById(placeId)).willReturn(Optional.of(mock(Place.class)));
        given(inventoryRepository.findByPlaceIdWithProduct(placeId)).willReturn(List.of());

        // when
        List<Inventory> result = inventoryQueryService.findByPlaceId(placeId);

        // then
        assertThat(result).isEmpty();
    }
}
