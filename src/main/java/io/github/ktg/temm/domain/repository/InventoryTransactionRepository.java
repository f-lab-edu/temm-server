package io.github.ktg.temm.domain.repository;

import io.github.ktg.temm.domain.model.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
}
