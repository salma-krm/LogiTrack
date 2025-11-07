package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.Inventory;
import com.smartusers.logitrackapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByWarehouse_IdAndProduct_Id(Long warehouseId, Long productId);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.warehouse.id = :warehouseId")
    Inventory findInventoryByWarehouseAndProduct(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.quantityOnHand - i.quantityReserved > 0 ORDER BY i.warehouse.id ASC")
    List<Inventory> findAllAvailableByProduct(@Param("productId") Long productId);
}
