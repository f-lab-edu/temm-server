package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.exception.InventoryNotFoundException;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryDeleteService {

    private final InventoryRepository inventoryRepository;

    public void delete(Long placeId, Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new InventoryNotFoundException(inventoryId));
        if (!inventory.getPlace().getId().equals(placeId)) {
            throw new InventoryNotFoundException(inventoryId);
        }
        inventory.validateDeletable();
        inventoryRepository.delete(inventory);
    }
}
