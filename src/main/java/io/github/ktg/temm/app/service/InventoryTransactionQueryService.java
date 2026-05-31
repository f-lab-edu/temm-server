package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.domain.model.InventoryTransaction;
import io.github.ktg.temm.domain.repository.InventoryTransactionRepository;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryTransactionQueryService {

    private final PlaceRepository placeRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    public Page<InventoryTransaction> findByPlaceId(Long placeId, int page, int size) {
        placeRepository.findById(placeId)
            .orElseThrow(() -> new PlaceNotFoundException(placeId));

        return inventoryTransactionRepository.findByPlaceIdIncludingTransfers(
            placeId,
            PageRequest.of(page - 1, size)
        );
    }
}
