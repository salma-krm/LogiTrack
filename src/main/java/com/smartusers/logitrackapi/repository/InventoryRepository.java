package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // ⚠️ DEPRECATED : Peut retourner NonUniqueResultException si plusieurs inventaires
    // Gardez-la uniquement si vous êtes sûr qu'il n'y a qu'UN SEUL inventaire par warehouse/product
    @Deprecated
    Optional<Inventory> findByWarehouse_IdAndProduct_Id(Long warehouseId, Long productId);

    // ✅ RECOMMANDÉ : Retourne TOUS les inventaires pour un warehouse et un product
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.product.id = :productId")
    List<Inventory> findAllByWarehouse_IdAndProduct_Id(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId
    );

    // Tous les inventaires pour un produit
    List<Inventory> findAllByProduct_Id(Long productId);

    // Tous les inventaires disponibles (quantité > 0) pour un produit
    @Query("SELECT i FROM Inventory i " +
            "WHERE i.product.id = :productId " +
            "AND (i.quantityOnHand - i.quantityReserved) > 0 " +
            "ORDER BY i.warehouse.id ASC")
    List<Inventory> findAllAvailableByProduct(@Param("productId") Long productId);
}