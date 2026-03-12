package io.github.ktg.temm.app.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StoreMemberAddRequest(
    @NotNull UUID userId
) {
}
