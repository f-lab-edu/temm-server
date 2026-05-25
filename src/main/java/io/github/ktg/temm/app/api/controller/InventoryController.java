package io.github.ktg.temm.app.api.controller;

import io.github.ktg.temm.app.aop.CheckStorePermission;
import io.github.ktg.temm.app.api.dto.InventoryAdjustRequest;
import io.github.ktg.temm.app.api.dto.InventoryCreateRequest;
import io.github.ktg.temm.app.api.dto.InventoryInboundRequest;
import io.github.ktg.temm.app.api.dto.InventoryOutboundRequest;
import io.github.ktg.temm.app.api.dto.InventoryResponse;
import io.github.ktg.temm.app.api.dto.InventoryTransferRequest;
import io.github.ktg.temm.app.service.InventoryAdjustService;
import io.github.ktg.temm.app.service.InventoryCreateService;
import io.github.ktg.temm.app.service.InventoryDeleteService;
import io.github.ktg.temm.app.service.InventoryInboundService;
import io.github.ktg.temm.app.service.InventoryOutboundService;
import io.github.ktg.temm.app.service.InventoryQueryService;
import io.github.ktg.temm.app.service.InventoryTransferService;
import io.github.ktg.temm.domain.model.Authorization;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/places/{placeId}/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryCreateService inventoryCreateService;
    private final InventoryQueryService inventoryQueryService;
    private final InventoryInboundService inventoryInboundService;
    private final InventoryOutboundService inventoryOutboundService;
    private final InventoryAdjustService inventoryAdjustService;
    private final InventoryTransferService inventoryTransferService;
    private final InventoryDeleteService inventoryDeleteService;

    @PostMapping
    @CheckStorePermission(placeId = "#placeId", role = Authorization.MEMBER)
    public ResponseEntity<Void> create(
        @PathVariable Long placeId,
        @RequestBody @Valid InventoryCreateRequest request
    ) {
        inventoryCreateService.create(request.toCommand(placeId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @CheckStorePermission(placeId = "#placeId", role = Authorization.MEMBER)
    public ResponseEntity<List<InventoryResponse>> list(@PathVariable Long placeId) {
        List<InventoryResponse> result = inventoryQueryService.findByPlaceId(placeId).stream()
            .map(InventoryResponse::from)
            .toList();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{inventoryId}/inbound")
    @CheckStorePermission(placeId = "#placeId", role = Authorization.MEMBER)
    public ResponseEntity<Void> inbound(
        @PathVariable Long placeId,
        @PathVariable Long inventoryId,
        @RequestBody @Valid InventoryInboundRequest request
    ) {
        inventoryInboundService.inbound(request.toCommand(placeId, inventoryId));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{inventoryId}/outbound")
    @CheckStorePermission(placeId = "#placeId", role = Authorization.MEMBER)
    public ResponseEntity<Void> outbound(
        @PathVariable Long placeId,
        @PathVariable Long inventoryId,
        @RequestBody @Valid InventoryOutboundRequest request
    ) {
        inventoryOutboundService.outbound(request.toCommand(placeId, inventoryId));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{inventoryId}/adjust")
    @CheckStorePermission(placeId = "#placeId", role = Authorization.MEMBER)
    public ResponseEntity<Void> adjust(
        @PathVariable Long placeId,
        @PathVariable Long inventoryId,
        @RequestBody @Valid InventoryAdjustRequest request
    ) {
        inventoryAdjustService.adjust(request.toCommand(placeId, inventoryId));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{inventoryId}/transfer")
    @CheckStorePermission(placeId = "#placeId", role = Authorization.MEMBER)
    public ResponseEntity<Void> transfer(
        @PathVariable Long placeId,
        @PathVariable Long inventoryId,
        @RequestBody @Valid InventoryTransferRequest request
    ) {
        inventoryTransferService.transfer(request.toCommand(placeId, inventoryId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{inventoryId}")
    @CheckStorePermission(placeId = "#placeId", role = Authorization.MEMBER)
    public ResponseEntity<Void> delete(
        @PathVariable Long placeId,
        @PathVariable Long inventoryId
    ) {
        inventoryDeleteService.delete(placeId, inventoryId);
        return ResponseEntity.ok().build();
    }
}
