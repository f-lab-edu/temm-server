package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceDeleteService {

    private final PlaceRepository placeRepository;

    public void delete(Long placeId) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new PlaceNotFoundException(placeId));

        place.validateDeletable();
        placeRepository.delete(place);
    }
}
