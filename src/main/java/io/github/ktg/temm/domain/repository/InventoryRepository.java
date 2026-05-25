package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.Inventory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    boolean existsByPlaceIdAndProductId(Long placeId, Long productId);

    @Query("SELECT i FROM Inventory i JOIN FETCH i.product WHERE i.place.id = :placeId")
    List<Inventory> findByPlaceIdWithProduct(@Param("placeId") Long placeId);

    @Query("SELECT i FROM Inventory i WHERE i.place.id = :placeId AND i.product.id = :productId")
    Optional<Inventory> findByPlaceIdAndProductId(
        @Param("placeId") Long placeId, @Param("productId") Long productId);
}
