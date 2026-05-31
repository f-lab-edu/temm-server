package io.github.ktg.temm.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import io.github.ktg.temm.domain.dto.ProductSearchCondition;
import io.github.ktg.temm.domain.dto.ProductSearchResult;
import io.github.ktg.temm.domain.repository.ProductQueryRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTest {

    @Mock
    ProductQueryRepository productQueryRepository;

    ProductQueryService productQueryService;

    @BeforeEach
    void setUp() {
        productQueryService = new ProductQueryService(productQueryRepository);
    }

    @Test
    @DisplayName("첫 번째 페이지 요청은 오프셋 없이 검색")
    void searchFirstPage() {
        // given
        ProductSearchCondition condition = new ProductSearchCondition(2L, null, null);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        given(productQueryRepository.search(eq(condition), pageableCaptor.capture()))
            .willReturn(new PageImpl<ProductSearchResult>(List.of()));

        // when
        productQueryService.search(condition, 1, 50);

        // then
        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getOffset()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(50);
    }

    @Test
    @DisplayName("두 번째 페이지 요청은 한 페이지 크기만큼 건너뛰고 검색")
    void searchSecondPage() {
        // given
        ProductSearchCondition condition = new ProductSearchCondition(2L, null, null);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        given(productQueryRepository.search(eq(condition), pageableCaptor.capture()))
            .willReturn(new PageImpl<ProductSearchResult>(List.of()));

        // when
        productQueryService.search(condition, 2, 50);

        // then
        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getOffset()).isEqualTo(50);
    }
}
