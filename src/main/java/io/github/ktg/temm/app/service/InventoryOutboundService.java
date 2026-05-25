package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.InventoryOutboundCommand;
import io.github.ktg.temm.app.exception.InventoryNotFoundException;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.model.InventoryTransaction;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import io.github.ktg.temm.domain.repository.InventoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryOutboundService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    public void outbound(InventoryOutboundCommand command) {
        Inventory inventory = findInventoryInPlace(command.inventoryId(), command.placeId());
        inventory.outbound(command.quantity());
        inventoryTransactionRepository.save(
            InventoryTransaction.outbound(
                inventory.getProduct().getId(), command.placeId(), command.quantity())
        );
    }

    private Inventory findInventoryInPlace(Long inventoryId, Long placeId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
            .orElseThrow(() -> new InventoryNotFoundException(inventoryId));
        if (!inventory.getPlace().getId().equals(placeId)) {
            throw new InventoryNotFoundException(inventoryId);
        }
        return inventory;
    }
}
