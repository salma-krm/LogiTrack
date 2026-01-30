package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
    Product findBySku(String sku);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.sku = :newSku WHERE p.id = :id")
    Product updateSkuById(@Param("id") Long id, @Param("newSku") String newSku);

    List<Product> findByNameContainingIgnoreCase(String name);

    // Nouvelle méthode pour charger tous les produits avec leurs catégories
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
    List<Product> findAllWithCategories();

    // Nouvelle méthode pour charger un produit avec sa catégorie
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);
}