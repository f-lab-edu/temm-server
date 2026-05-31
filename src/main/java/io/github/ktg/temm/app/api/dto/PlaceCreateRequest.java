package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.PlaceCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlaceCreateRequest(
    @NotNull Long storeId,
    @NotBlank String name
) {

    public PlaceCreateCommand toCommand() {
        return new PlaceCreateCommand(storeId, name);
    }
}
