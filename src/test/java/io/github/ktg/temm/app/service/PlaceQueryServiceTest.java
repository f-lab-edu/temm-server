package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceQueryServiceTest {

    @Mock
    PlaceRepository placeRepository;

    PlaceQueryService placeQueryService;

    @BeforeEach
    void setUp() {
        placeQueryService = new PlaceQueryService(placeRepository);
    }

    @Test
    @DisplayName("스토어에 속한 장소 목록 조회")
    void findByStoreId() {
        // given
        Long storeId = 1L;
        List<Place> places = List.of(mock(Place.class), mock(Place.class));
        given(placeRepository.findByStoreId(storeId)).willReturn(places);

        // when
        List<Place> result = placeQueryService.findByStoreId(storeId);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("장소가 없으면 빈 목록 반환")
    void findByStoreIdEmpty() {
        // given
        Long storeId = 1L;
        given(placeRepository.findByStoreId(storeId)).willReturn(List.of());

        // when
        List<Place> result = placeQueryService.findByStoreId(storeId);

        // then
        assertThat(result).isEmpty();
    }
}
