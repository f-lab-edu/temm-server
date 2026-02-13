package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
