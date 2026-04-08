package io.github.ktg.temm.app.api.controller;

import io.github.ktg.temm.app.api.dto.UserStoreResponse;
import io.github.ktg.temm.app.security.LoginContext;
import io.github.ktg.temm.app.service.UserStoreQueryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserStoreController {

    private final UserStoreQueryService userStoreQueryService;

    @GetMapping("/stores")
    public ResponseEntity<List<UserStoreResponse>> getMyStores() {
        UUID userId = UUID.fromString(LoginContext.get());
        List<UserStoreResponse> response = userStoreQueryService.searchByUserId(userId)
            .stream()
            .map(UserStoreResponse::from)
            .toList();
        return ResponseEntity.ok(response);
    }
}