package io.github.ktg.temm.app.service;

import io.github.ktg.temm.app.dto.StoreCreateCommand;
import io.github.ktg.temm.app.exception.UserNotFoundException;
import io.github.ktg.temm.domain.model.Store;
import io.github.ktg.temm.domain.model.User;
import io.github.ktg.temm.domain.repository.StoreRepository;
import io.github.ktg.temm.domain.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreCreateService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public void create(StoreCreateCommand command) {
        UUID userId = command.userId();
        User user = getUser(userId);

        Store store = Store.create(command.name(), user);
        storeRepository.save(store);
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }


}
