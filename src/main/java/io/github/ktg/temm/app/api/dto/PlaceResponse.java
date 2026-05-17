package io.github.ktg.temm.app.api.dto;

import io.github.ktg.temm.domain.model.Place;

public record PlaceResponse(Long id, String name) {

    public static PlaceResponse from(Place place) {
        return new PlaceResponse(place.getId(), place.getName());
    }
}
