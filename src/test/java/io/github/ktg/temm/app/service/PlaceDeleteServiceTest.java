package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.exception.PlaceHasStockException;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceDeleteServiceTest {

    @Mock
    PlaceRepository placeRepository;

    PlaceDeleteService placeDeleteService;

    @BeforeEach
    void setUp() {
        placeDeleteService = new PlaceDeleteService(placeRepository);
    }

    @Test
    @DisplayName("장소가 없으면 예외")
    void deleteFailPlaceNotFound() {
        // given
        Long placeId = 1L;
        given(placeRepository.findById(placeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> placeDeleteService.delete(placeId))
            .isInstanceOf(PlaceNotFoundException.class)
            .hasMessageContaining(ErrorCode.PLACE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고가 남아있으면 삭제 불가 예외")
    void deleteFailHasStock() {
        // given
        Long placeId = 1L;
        Place place = mock(Place.class);
        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));
        willThrow(new PlaceHasStockException()).given(place).validateDeletable();

        // when
        // then
        assertThatThrownBy(() -> placeDeleteService.delete(placeId))
            .isInstanceOf(PlaceHasStockException.class)
            .hasMessageContaining(ErrorCode.PLACE_HAS_STOCK.getMessage());
    }

    @Test
    @DisplayName("장소 삭제 성공")
    void deleteSuccess() {
        // given
        Long placeId = 1L;
        Place place = mock(Place.class);
        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));

        // when
        placeDeleteService.delete(placeId);

        // then
        then(placeRepository).should().delete(place);
    }
}
