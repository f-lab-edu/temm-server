package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.PlaceUpdateCommand;
import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceUpdateService {

    private final PlaceRepository placeRepository;

    public void update(Long placeId, PlaceUpdateCommand command) {
        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new PlaceNotFoundException(placeId));

        place.changeName(command.name());
    }
}
