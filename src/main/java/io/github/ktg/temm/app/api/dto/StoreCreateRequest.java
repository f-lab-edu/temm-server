package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.StoreCreateCommand;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record StoreCreateRequest(
    @NotBlank String name
) {

    public StoreCreateCommand toCommand(UUID userId) {
        return new StoreCreateCommand(userId, name);
    }
}
