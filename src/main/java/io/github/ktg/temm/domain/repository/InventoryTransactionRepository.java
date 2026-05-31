package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    @Query(
        value = """
            SELECT t FROM InventoryTransaction t
            WHERE t.placeId = :placeId
               OR t.fromPlaceId = :placeId
               OR t.toPlaceId = :placeId
            ORDER BY t.createdAt DESC, t.id DESC
            """,
        countQuery = """
             SELECT COUNT(t) FROM InventoryTransaction t
            WHERE t.placeId = :placeId
               OR t.fromPlaceId = :placeId
               OR t.toPlaceId = :placeId
            """
    )
    Page<InventoryTransaction> findByPlaceIdIncludingTransfers(
        @Param("placeId") Long placeId,
        Pageable pageable
    );
}
