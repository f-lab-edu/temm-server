package io.github.ktg.temm.app.dto;

import java.util.List;

public record ProductUpdateCommand(
    String name,
    String sku,
    String barcode,
    String imageUrl,
    List<Long> categoryIds
) {
}