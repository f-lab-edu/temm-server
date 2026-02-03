package io.github.ktg.temm.domain.model;

import io.github.ktg.temm.domain.exception.EmailNotValidException;
import io.github.ktg.temm.domain.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public record Email(
    @Column(name = "email", nullable = false, length = 100)
    String value
) {

    private static final Pattern PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");

    public Email {
        value = normalize(value);
        validate(value);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase();
    }

    private void validate(String value) {
        validateNullOrEmpty(value);
        validatePattern(value);
    }

    private void validateNullOrEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new EmailNotValidException(ErrorCode.EMAIL_IS_REQUIRED);
        }
    }

    private void validatePattern(String value) {
        if (!PATTERN.matcher(value).matches()) {
            throw new EmailNotValidException(ErrorCode.EMAIL_PATTERN_NOT_MATCHED, value);
        }
    }

}
