package io.github.ktg.temm.app.service;

import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceQueryService {

    private final PlaceRepository placeRepository;

    public List<Place> findByStoreId(Long storeId) {
        return placeRepository.findByStoreId(storeId);
    }
}
