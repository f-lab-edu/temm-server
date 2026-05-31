package io.github.ktg.temm.app.api.controller;

import io.github.ktg.temm.app.aop.CheckStorePermission;
import io.github.ktg.temm.app.api.dto.PlaceCreateRequest;
import io.github.ktg.temm.app.api.dto.PlaceResponse;
import io.github.ktg.temm.app.api.dto.PlaceUpdateRequest;
import io.github.ktg.temm.app.service.PlaceCreateService;
import io.github.ktg.temm.app.service.PlaceDeleteService;
import io.github.ktg.temm.app.service.PlaceQueryService;
import io.github.ktg.temm.app.service.PlaceUpdateService;
import io.github.ktg.temm.domain.model.Authorization;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceCreateService placeCreateService;
    private final PlaceUpdateService placeUpdateService;
    private final PlaceDeleteService placeDeleteService;
    private final PlaceQueryService placeQueryService;

    @PostMapping
    @CheckStorePermission(storeId = "#request.storeId", role = Authorization.MEMBER)
    public ResponseEntity<Void> create(@RequestBody @Valid PlaceCreateRequest request) {
        placeCreateService.create(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{placeId}")
    @CheckStorePermission(placeId = "#placeId", role = Authorization.MEMBER)
    public ResponseEntity<Void> update(@PathVariable Long placeId, @RequestBody @Valid PlaceUpdateRequest request) {
        placeUpdateService.update(placeId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{placeId}")
    @CheckStorePermission(placeId = "#placeId", role = Authorization.MEMBER)
    public ResponseEntity<Void> delete(@PathVariable Long placeId) {
        placeDeleteService.delete(placeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @CheckStorePermission(storeId = "#storeId", role = Authorization.MEMBER)
    public ResponseEntity<List<PlaceResponse>> list(@RequestParam Long storeId) {
        List<PlaceResponse> result = placeQueryService.findByStoreId(storeId).stream()
            .map(PlaceResponse::from)
            .toList();
        return ResponseEntity.ok(result);
    }
}
