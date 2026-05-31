package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import io.github.ktg.temm.app.dto.PlaceUpdateCommand;
import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceUpdateServiceTest {

    @Mock
    PlaceRepository placeRepository;

    PlaceUpdateService placeUpdateService;

    @BeforeEach
    void setUp() {
        placeUpdateService = new PlaceUpdateService(placeRepository);
    }

    @Test
    @DisplayName("장소가 없으면 예외")
    void updateFailPlaceNotFound() {
        // given
        Long placeId = 1L;
        given(placeRepository.findById(placeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> placeUpdateService.update(placeId, new PlaceUpdateCommand("창고 B")))
            .isInstanceOf(PlaceNotFoundException.class)
            .hasMessageContaining(ErrorCode.PLACE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장소 이름 변경")
    void updateName() {
        // given
        Long placeId = 1L;
        Place place = Place.create(mock(Store.class), "창고 A");
        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));

        // when
        placeUpdateService.update(placeId, new PlaceUpdateCommand("창고 B"));

        // then
        assertThat(place.getName()).isEqualTo("창고 B");
    }
}
