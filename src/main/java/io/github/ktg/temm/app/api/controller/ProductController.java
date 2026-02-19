package io.github.ktg.temm.app.api.controller;

import io.github.ktg.temm.app.api.dto.PageResponse;
import io.github.ktg.temm.app.api.dto.ProductDetailResponse;
import io.github.ktg.temm.app.api.dto.ProductRegisterRequest;
import io.github.ktg.temm.app.api.dto.ProductSearchRequest;
import io.github.ktg.temm.app.api.dto.ProductSearchResponse;
import io.github.ktg.temm.app.api.dto.ProductUpdateRequest;
import io.github.ktg.temm.app.service.ProductLifecycleService;
import io.github.ktg.temm.app.service.ProductQueryService;
import io.github.ktg.temm.app.service.ProductRegisterService;
import io.github.ktg.temm.app.service.ProductUpdateService;
import io.github.ktg.temm.domain.dto.ProductDetailResult;
import io.github.ktg.temm.domain.dto.ProductSearchResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRegisterService productRegisterService;
    private final ProductUpdateService productUpdateService;
    private final ProductLifecycleService productLifecycleService;
    private final ProductQueryService productQueryService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Valid ProductRegisterRequest request) {
        productRegisterService.register(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> update(@PathVariable Long productId, @RequestBody @Valid ProductUpdateRequest request) {
        productUpdateService.update(productId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{productId}/stop")
    public ResponseEntity<Void> stop(@PathVariable Long productId) {
        productLifecycleService.stop(productId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{productId}/register")
    public ResponseEntity<Void> reRegister(@PathVariable Long productId) {
        productLifecycleService.register(productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productLifecycleService.delete(productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getDetail(@PathVariable Long productId) {
        ProductDetailResult result = productQueryService.getDetail(productId);
        return ResponseEntity.ok(ProductDetailResponse.from(result));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductSearchResponse>> search(@Valid ProductSearchRequest request) {
        int pageNumber = Math.max(request.page() - 1, 0);
        Page<ProductSearchResult> result = productQueryService.search(request.toCondition(), pageNumber, request.size());
        Page<ProductSearchResponse> responsePage = result.map(ProductSearchResponse::from);
        return ResponseEntity.ok(PageResponse.from(responsePage));
    }

}
