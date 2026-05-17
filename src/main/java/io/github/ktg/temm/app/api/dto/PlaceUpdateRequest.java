package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.app.dto.PlaceUpdateCommand;
import jakarta.validation.constraints.NotBlank;

public record PlaceUpdateRequest(
    @NotBlank String name
) {

    public PlaceUpdateCommand toCommand() {
        return new PlaceUpdateCommand(name);
    }
}
