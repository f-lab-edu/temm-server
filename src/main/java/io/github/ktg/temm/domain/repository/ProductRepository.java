package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.Product;
import io.github.ktg.temm.domain.model.Sku;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByStoreIdAndSku(Long storeId, Sku sku);

}
