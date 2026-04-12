package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.StoreUpdateCommand;
import jakarta.validation.constraints.NotBlank;

public record StoreUpdateRequest(
    @NotBlank String name
) {

    public StoreUpdateCommand toCommand() {
        return new StoreUpdateCommand(name);
    }
}
