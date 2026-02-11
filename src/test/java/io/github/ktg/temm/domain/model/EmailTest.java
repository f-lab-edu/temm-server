package io.github.ktg.temm.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.ktg.temm.domain.exception.EmailNotValidException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    @DisplayName("이메일은 입력 시 trim + lowercase 처리")
    void emailInputNormalized() {
        // given
        String input = "  test@test.COm  ";
        // when
        Email email = new Email(input);
        // then
        String value = email.value();
        assertThat(value).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("이메일은 null 미허용")
    void emailIsNotNull() {
        // given
        String input = null;
        // when
        // then
        assertThatThrownBy(() -> new Email(input))
            .isInstanceOf(EmailNotValidException.class)
            .hasMessageContaining(ErrorCode.EMAIL_IS_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("이메일은 빈 값 미허용")
    void emailIsNotEmpty() {
        // given
        String input = " ";
        // when
        // then
        assertThatThrownBy(() -> new Email(input))
            .isInstanceOf(EmailNotValidException.class)
            .hasMessageContaining(ErrorCode.EMAIL_IS_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("이메일은 형식에 맞게 입력 (맞게 입력)")
    void formattedEmail() {
        // given
        String input = "test@test.com";
        // when
        Email email = new Email(input);
        // then
        assertThat(email.value()).isEqualTo("test@test.com");
    }
    
    @Test
    @DisplayName("이메일은 '@'가 필수")
    void emailRequiredAt() {
        // given
        String input = "test_test.com";
        // when
        // then
        assertThatThrownBy(() -> new Email(input))
            .isInstanceOf(EmailNotValidException.class)
            .hasMessageContaining(ErrorCode.EMAIL_PATTERN_NOT_MATCHED.getMessage())
            .hasMessageContaining(input);
    }
    
    @Test
    @DisplayName("이메일은 TLD가 두글자 이상")
    void emailMustBeLeastTwoTLD() {
        // given
        String input = "test@test.c";
        // when
        // then
        assertThatThrownBy(() -> new Email(input))
            .isInstanceOf(EmailNotValidException.class)
            .hasMessageContaining(ErrorCode.EMAIL_PATTERN_NOT_MATCHED.getMessage())
            .hasMessageContaining(input);
    }

}