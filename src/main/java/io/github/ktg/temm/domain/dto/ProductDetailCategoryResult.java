package io.github.ktg.temm.domain.dto;

import io.github.ktg.temm.domain.model.Category;
import io.github.ktg.temm.domain.model.CategoryProduct;

public record ProductDetailCategoryResult(Long id, String name) {

    public static ProductDetailCategoryResult from(CategoryProduct categoryProduct) {
        Category category = categoryProduct.getCategory();
        return new ProductDetailCategoryResult(category.getId(), category.getName());
    }

}
