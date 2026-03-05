package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.github.ktg.temm.app.exception.ProductNotFoundException;
import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductLifecycleServiceTest {

    @InjectMocks
    private ProductLifecycleService productLifecycleService;

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 판매 중지")
    void stop() {
        // given
        Long productId = 1L;
        Product product = mock(Product.class);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        productLifecycleService.stop(productId);

        // then
        verify(product).stop();
    }

    @Test
    @DisplayName("상품 삭제")
    void delete() {
        // given
        Long productId = 1L;
        Product product = mock(Product.class);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        productLifecycleService.delete(productId);

        // then
        verify(product).delete();
    }

    @Test
    @DisplayName("상품 재등록")
    void register() {
        // given
        Long productId = 1L;
        Product product = mock(Product.class);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        productLifecycleService.register(productId);

        // then
        verify(product).register();
    }

    @Test
    @DisplayName("상품 없음 예외")
    void productNotFound() {
        // given
        Long productId = 1L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> productLifecycleService.stop(productId))
            .isInstanceOf(ProductNotFoundException.class);
    }
}
