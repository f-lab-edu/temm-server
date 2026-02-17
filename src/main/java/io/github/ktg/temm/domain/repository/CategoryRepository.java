package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByIdIn(List<Long> categoryIds);

}
