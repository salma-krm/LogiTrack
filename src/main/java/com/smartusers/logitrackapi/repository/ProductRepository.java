package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
    Product findBySku(String sku);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.sku = :newSku WHERE p.id = :id")
    Product updateSkuById(@Param("id") Long id, @Param("newSku") String newSku);

    List<Product> findByNameContainingIgnoreCase(String name);
}