package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.Place;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("SELECT p FROM Place p WHERE p.store.id = :storeId")
    List<Place> findByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT p.store.id FROM Place p WHERE p.id = :placeId")
    Optional<Long> findStoreIdById(@Param("placeId") Long placeId);
}
