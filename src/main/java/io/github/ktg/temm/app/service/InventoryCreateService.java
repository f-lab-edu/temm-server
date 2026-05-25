package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.InventoryCreateCommand;
import io.github.ktg.temm.app.exception.InventoryDuplicateException;
import io.github.ktg.temm.app.exception.PlaceNotFoundException;
import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.app.exception.ProductNotInStoreException;
import io.github.ktg.temm.domain.model.Inventory;
import io.github.ktg.temm.domain.model.Place;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.repository.InventoryRepository;
import io.github.ktg.temm.domain.repository.PlaceRepository;
import io.github.ktg.temm.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryCreateService {

    private final PlaceRepository placeRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public void create(InventoryCreateCommand command) {
        Place place = placeRepository.findById(command.placeId())
            .orElseThrow(() -> new PlaceNotFoundException(command.placeId()));

        Product product = productRepository.findById(command.productId())
            .orElseThrow(() -> new ProductNotFoundException(command.productId()));

        if (!product.getStoreId().equals(place.getStore().getId())) {
            throw new ProductNotInStoreException(command.productId(), place.getStore().getId());
        }

        if (inventoryRepository.existsByPlaceIdAndProductId(command.placeId(), command.productId())) {
            throw new InventoryDuplicateException(command.placeId(), command.productId());
        }

        Inventory inventory = Inventory.create(place, product);
        inventoryRepository.save(inventory);
    }
}
