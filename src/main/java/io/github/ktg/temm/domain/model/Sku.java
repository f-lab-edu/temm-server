package io.github.ktg.temm.domain.model;

import io.github.ktg.temm.domain.exception.ErrorCode;
import io.github.ktg.temm.domain.exception.SkuNotValidException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public record Sku(
    @Column(name = "sku", nullable = false)
    String value
) {

    private static final Pattern PATTERN = Pattern.compile(
        "^[a-zA-Z0-9-_]+$");

    public Sku {
        value = normalize(value);
        validate(value);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    private void validate(String value) {
        validateNullOrEmpty(value);
        validatePattern(value);
    }

    private void validateNullOrEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new SkuNotValidException(ErrorCode.SKU_IS_REQUIRED);
        }
    }

    private void validatePattern(String value) {
        if (!PATTERN.matcher(value).matches()) {
            throw new SkuNotValidException(ErrorCode.SKU_PATTERN_NOT_MATCHED);
        }
    }

}
