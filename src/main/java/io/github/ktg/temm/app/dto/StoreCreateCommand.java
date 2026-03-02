package io.github.ktg.temm.app.dto;

import java.util.UUID;

public record StoreCreateCommand(
    UUID userId,
    String name
) {

}
