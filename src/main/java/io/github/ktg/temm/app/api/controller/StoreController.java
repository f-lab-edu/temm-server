package io.github.ktg.temm.app.api.controller;

import io.github.ktg.temm.app.aop.CheckStorePermission;
import io.github.ktg.temm.app.api.dto.StoreCreateRequest;
import io.github.ktg.temm.app.api.dto.StoreMemberAddRequest;
import io.github.ktg.temm.app.api.dto.StoreUpdateRequest;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.service.StoreCreateService;
import io.github.ktg.temm.app.service.StoreMemberService;
import io.github.ktg.temm.app.service.StoreUpdateService;
import io.github.ktg.temm.domain.model.Authorization;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreCreateService storeCreateService;
    private final StoreUpdateService storeUpdateService;
    private final StoreMemberService storeMemberService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid StoreCreateRequest request) {
        UUID loginUserId = UUID.fromString(LoginContext.get());
        storeCreateService.create(request.toCommand(loginUserId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{storeId}")
    @CheckStorePermission(storeId = "#storeId", role = Authorization.MANAGER)
    public ResponseEntity<Void> update(@PathVariable Long storeId, @RequestBody @Valid StoreUpdateRequest request) {
        storeUpdateService.update(storeId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{storeId}/members")
    @CheckStorePermission(storeId = "#storeId", role = Authorization.MANAGER)
    public ResponseEntity<Void> addMember(@PathVariable Long storeId, @RequestBody @Valid StoreMemberAddRequest request) {
        storeMemberService.addMember(storeId, request.userId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{storeId}/members/{userId}")
    @CheckStorePermission(storeId = "#storeId", role = Authorization.MANAGER)
    public ResponseEntity<Void> removeMember(@PathVariable Long storeId, @PathVariable UUID userId) {
        storeMemberService.removeMember(storeId, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{storeId}/members/{userId}/manager")
    @CheckStorePermission(storeId = "#storeId", role = Authorization.MANAGER)
    public ResponseEntity<Void> grantManagerRole(@PathVariable Long storeId, @PathVariable UUID userId) {
        storeMemberService.grantManagerRole(storeId, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{storeId}/members/{userId}/member")
    @CheckStorePermission(storeId = "#storeId", role = Authorization.MANAGER)
    public ResponseEntity<Void> grantMemberRole(@PathVariable Long storeId, @PathVariable UUID userId) {
        storeMemberService.grantMemberRole(storeId, userId);
        return ResponseEntity.ok().build();
    }
}
