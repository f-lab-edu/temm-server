package io.github.ktg.temm.app.dto;

public record PlaceCreateCommand(
    Long storeId,
    String name
) {

}
