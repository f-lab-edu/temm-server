package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.github.ktg.temm.app.dto.ProductUpdateCommand;
import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.app.exception.StoreSkuDuplicateException;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.Sku;
import io.github.ktg.temm.domain.repository.CategoryRepository;
import io.github.ktg.temm.domain.repository.ProductRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductUpdateServiceTest {

    @InjectMocks
    private ProductUpdateService productUpdateService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("물품 수정 성공")
    void update() {
        // given
        Long productId = 1L;
        Product product = mock(Product.class);
        given(product.getStoreId()).willReturn(1L);
        given(product.getSku()).willReturn(new Sku("OLD-SKU"));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productRepository.existsByStoreIdAndSku(any(), any())).willReturn(false);
        given(categoryRepository.findByIdIn(anyList())).willReturn(Collections.emptyList());

        ProductUpdateCommand command = new ProductUpdateCommand(
            "new name",
            "NEW-SKU",
            "new barcode",
            "new image url",
            List.of(1L, 2L)
        );

        // when
        productUpdateService.update(productId, command);

        // then
        verify(product).changeName(command.name());
        verify(product).changeSku(command.sku());
        verify(product).changeBarcode(command.barcode());
        verify(product).changeImage(command.imageUrl());
        verify(product).changeCategories(anyList());
    }

    @Test
    @DisplayName("물품 수정 시 물품이 없으면 예외")
    void updateFailProductNotFound() {
        // given
        Long productId = 1L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        ProductUpdateCommand command = new ProductUpdateCommand(
            "new name",
            "NEW-SKU",
            "new barcode",
            "new image url",
            List.of(1L, 2L)
        );

        // when
        // then
        assertThatThrownBy(() -> productUpdateService.update(productId, command))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining("" + productId);
    }

    @Test
    @DisplayName("물품 수정 시 SKU 중복이면 예외")
    void updateFailSkuDuplicate() {
        // given
        Long productId = 1L;
        Product product = mock(Product.class);
        given(product.getStoreId()).willReturn(1L);
        given(product.getSku()).willReturn(new Sku("OLD-SKU"));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productRepository.existsByStoreIdAndSku(any(), any())).willReturn(true);

        ProductUpdateCommand command = new ProductUpdateCommand(
            "new name",
            "NEW-SKU",
            "new barcode",
            "new image url",
            List.of(1L, 2L)
        );

        // when
        // then
        assertThatThrownBy(() -> productUpdateService.update(productId, command))
            .isInstanceOf(StoreSkuDuplicateException.class);
    }
}
