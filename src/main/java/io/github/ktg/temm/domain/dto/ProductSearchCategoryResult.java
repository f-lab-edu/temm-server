package io.github.ktg.temm.domain.dto;

import io.github.ktg.temm.domain.model.Category;
import io.github.ktg.temm.domain.model.CategoryProduct;

public record ProductSearchCategoryResult(Long id, String name) {

    public static ProductSearchCategoryResult from(CategoryProduct categoryProduct) {
        Category category = categoryProduct.getCategory();
        return new ProductSearchCategoryResult(category.getId(), category.getName());
    }

}
