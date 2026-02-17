package io.github.ktg.temm.app.dto;

import java.util.List;

public record ProductRegisterCommand(
    Long storeId,
    List<Long> categoryIds,
    String productName,
    String sku,
    String barcode,
    String imageUrl
) {

}