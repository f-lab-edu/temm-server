package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.StoreUpdateCommand;
import io.github.ktg.temm.app.exception.StoreNotFoundException;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreUpdateService {

    private final StoreRepository storeRepository;

    public void update(Long storeId, StoreUpdateCommand command) {
        Store store = getStore(storeId);
        store.changeName(command.name());
    }

    private Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> new StoreNotFoundException(storeId));
    }

}
