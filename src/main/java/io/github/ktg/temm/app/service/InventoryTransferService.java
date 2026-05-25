package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.InventoryTransferCommand;
import io.github.ktg.temm.app.exception.InventoryNotFoundException;
import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.app.exception.PlaceNotInSameStoreException;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.model.InventoryTransaction;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import io.github.ktg.temm.domain.repository.InventoryTransactionRepository;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryTransferService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final PlaceRepository placeRepository;

    public void transfer(InventoryTransferCommand command) {
        Inventory sourceInventory = findInventoryInPlace(command.inventoryId(), command.placeId());
        Place fromPlace = sourceInventory.getPlace();

        Place toPlace = placeRepository.findById(command.toPlaceId())
            .orElseThrow(() -> new PlaceNotFoundException(command.toPlaceId()));

        if (!fromPlace.getStore().getId().equals(toPlace.getStore().getId())) {
            throw new PlaceNotInSameStoreException(command.placeId(), command.toPlaceId());
        }

        Product product = sourceInventory.getProduct();
        sourceInventory.outbound(command.quantity());

        Inventory targetInventory = inventoryRepository
            .findByPlaceIdAndProductId(command.toPlaceId(), product.getId())
            .orElseGet(() -> inventoryRepository.save(Inventory.create(toPlace, product)));

        targetInventory.inbound(command.quantity());

        inventoryTransactionRepository.save(
            InventoryTransaction.transfer(
                product.getId(), command.placeId(), command.toPlaceId(), command.quantity())
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
