package io.github.ktg.temm.app.service;



import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import io.github.ktg.temm.app.dto.ProductRegisterCommand;
import io.github.ktg.temm.app.exception.StoreSkuDuplicateException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.model.Category;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.Sku;
import io.github.ktg.temm.domain.repository.CategoryRepository;
import io.github.ktg.temm.domain.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductRegisterServiceTest {

    ProductRegisterService productRegisterService;

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        productRegisterService = new ProductRegisterService(productRepository, categoryRepository);
    }

    @Test
    @DisplayName("물품 등록 시 스토어 내 SKU 중복 되었을 때 예외")
    void registerThrowExceptionDuplicateStoreAndSku() {
        // given
        Long storeId = 1L;
        List<Long> categoryIds = List.of(1L);
        String skuInput = "KC-12";
        String name = "물품";
        String barcode = "barcode";
        String imageUrl = "s3://image/url";
        Sku sku = new Sku(skuInput);

        ProductRegisterCommand command = new ProductRegisterCommand(storeId,
            categoryIds, name, skuInput, barcode, imageUrl);
        given(productRepository.existsByStoreIdAndSku(storeId, sku))
                .willReturn(Boolean.TRUE);

        // when
        // then
        assertThatThrownBy(() -> productRegisterService.register(command))
            .isInstanceOf(StoreSkuDuplicateException.class)
            .hasMessageContaining(ErrorCode.STORE_SKU_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("물품 등록 성공 시 물품이 정상 저장")
    void registerSaveProductSuccess() {
        // given
        Long storeId = 1L;
        List<Long> categoryIds = List.of(1L);
        String skuInput = "KC-12";
        String name = "물품";
        String barcode = "barcode";
        String imageUrl = "s3://image/url";
        Sku sku = new Sku(skuInput);

        ProductRegisterCommand command = new ProductRegisterCommand(storeId,
            categoryIds, name, skuInput, barcode, imageUrl);
        given(productRepository.existsByStoreIdAndSku(storeId, sku))
            .willReturn(Boolean.FALSE);
        given(categoryRepository.findByStoreIdAndIdIn(storeId, categoryIds))
            .willReturn(List.of(mock(Category.class)));

        // then
        productRegisterService.register(command);

        // when
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        then(productRepository).should(times(1)).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getStoreId()).isEqualTo(storeId);
        assertThat(savedProduct.getName()).isEqualTo(name);
        assertThat(savedProduct.getSku()).isEqualTo(sku);
        assertThat(savedProduct.getBarcode()).isEqualTo(barcode);
        assertThat(savedProduct.getImageUrl()).isEqualTo(imageUrl);
        assertThat(savedProduct.getCategoryProducts().size()).isEqualTo(1);
    }


}