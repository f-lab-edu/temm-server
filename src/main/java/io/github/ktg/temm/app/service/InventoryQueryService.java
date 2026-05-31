package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryQueryService {

    private final PlaceRepository placeRepository;
    private final InventoryRepository inventoryRepository;

    public List<Inventory> findByPlaceId(Long placeId) {
        placeRepository.findById(placeId)
            .orElseThrow(() -> new PlaceNotFoundException(placeId));
        return inventoryRepository.findByPlaceIdWithProduct(placeId);
    }
}
