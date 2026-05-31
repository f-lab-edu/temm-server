package io.github.ktg.temm.app.service;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ktg.temm.app.dto.PlaceCreateCommand;
import io.github.ktg.temm.app.exception.StoreNotFoundException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import io.github.ktg.temm.domain.repository.StoreRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceCreateServiceTest {

    @Mock
    StoreRepository storeRepository;

    @Mock
    PlaceRepository placeRepository;

    PlaceCreateService placeCreateService;

    @BeforeEach
    void setUp() {
        placeCreateService = new PlaceCreateService(storeRepository, placeRepository);
    }

    @Test
    @DisplayName("스토어가 없으면 예외")
    void createFailStoreNotFound() {
        // given
        Long storeId = 1L;
        PlaceCreateCommand command = new PlaceCreateCommand(storeId, "창고 A");
        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> placeCreateService.create(command))
            .isInstanceOf(StoreNotFoundException.class)
            .hasMessageContaining(ErrorCode.STORE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장소 생성 시 저장")
    void createSaved() {
        // given
        Long storeId = 1L;
        PlaceCreateCommand command = new PlaceCreateCommand(storeId, "창고 A");
        given(storeRepository.findById(storeId)).willReturn(Optional.of(mock(Store.class)));

        // when
        placeCreateService.create(command);

        // then
        then(placeRepository).should(times(1)).save(any(Place.class));
    }
}
