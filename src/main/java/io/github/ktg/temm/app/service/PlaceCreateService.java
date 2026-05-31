package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.PlaceCreateCommand;
import io.github.ktg.temm.app.exception.StoreNotFoundException;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import io.github.ktg.temm.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceCreateService {

    private final StoreRepository storeRepository;
    private final PlaceRepository placeRepository;

    public void create(PlaceCreateCommand command) {
        Store store = storeRepository.findById(command.storeId())
            .orElseThrow(() -> new StoreNotFoundException(command.storeId()));

        Place place = Place.create(store, command.name());
        placeRepository.save(place);
    }
}
