package io.github.ktg.temm.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.exception.SkuNotValidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SkuTest {

    @Test
    @DisplayName("SKU 입력 시 trim 처리")
    void skuIsNormalized() {
        // given
        String input = " SKU ";
        // when
        Sku sku = new Sku(input);
        // then
        assertThat(sku.value()).isEqualTo("SKU");
    }

    @Test
    @DisplayName("SKU 정보는 null 미허용")
    void skuIsNotNull() {
        // given
        String input = null;
        // when
        // then
        assertThatThrownBy(() -> new Sku(input))
            .isInstanceOf(SkuNotValidException.class)
            .hasMessageContaining(ErrorCode.SKU_IS_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("SKU 정보는 빈 값 미허용")
    void skuIsNotEmpty() {
        // given
        String input = " ";
        // when
        // then
        assertThatThrownBy(() -> new Sku(input))
            .isInstanceOf(SkuNotValidException.class)
            .hasMessageContaining(ErrorCode.SKU_IS_REQUIRED.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A-abc_B", "123-456", "SKU_TEST", "a-b_c-1"})
    @DisplayName("SKU 정보는 대/소문자, 숫자, 대시, 언더바 허용")
    void shouldAllowValidCharacters(String input) {
        // given
        // when
        Sku sku = new Sku(input);

        // then
        assertThat(sku.value()).isEqualTo(input);
    }

    @ParameterizedTest
    @ValueSource(strings = {"!@#$", "(ABC)", "...?", "안녕"})
    @DisplayName("SKU 정보는 대/소문자, 숫자, 대시, 언더바 제외 미허용")
    void shouldFailWithInvalidCharacters(String input) {
        // given
        // when
        // then
        assertThatThrownBy(() -> new Sku(input))
            .isInstanceOf(SkuNotValidException.class)
            .hasMessageContaining(ErrorCode.SKU_PATTERN_NOT_MATCHED.getMessage());
    }


}