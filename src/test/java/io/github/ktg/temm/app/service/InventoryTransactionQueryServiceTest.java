package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.InventoryTransaction;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.repository.InventoryTransactionRepository;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class InventoryTransactionQueryServiceTest {

    @Mock
    PlaceRepository placeRepository;

    @Mock
    InventoryTransactionRepository inventoryTransactionRepository;

    InventoryTransactionQueryService inventoryTransactionQueryService;

    @BeforeEach
    void setUp() {
        inventoryTransactionQueryService = new InventoryTransactionQueryService(
            placeRepository,
            inventoryTransactionRepository
        );
    }

    @Test
    @DisplayName("장소를 찾을 수 없으면 거래 이력 조회 실패")
    void findByPlaceIdFailPlaceNotFound() {
        // given
        Long placeId = 1L;
        given(placeRepository.findById(placeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> inventoryTransactionQueryService.findByPlaceId(placeId, 1, 10))
            .isInstanceOf(PlaceNotFoundException.class)
            .hasMessageContaining(ErrorCode.PLACE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장소별 거래 이력 첫 페이지 조회")
    void findByPlaceId() {
        // given
        Long placeId = 1L;
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        List<InventoryTransaction> transactions = List.of(mock(InventoryTransaction.class));
        given(placeRepository.findById(placeId)).willReturn(Optional.of(mock(Place.class)));
        given(inventoryTransactionRepository.findByPlaceIdIncludingTransfers(
            eq(placeId),
            pageableCaptor.capture()
        )).willReturn(new PageImpl<>(transactions));

        // when
        Page<InventoryTransaction> result = inventoryTransactionQueryService.findByPlaceId(
            placeId,
            1,
            20
        );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(pageableCaptor.getValue().getOffset()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
    }
}
