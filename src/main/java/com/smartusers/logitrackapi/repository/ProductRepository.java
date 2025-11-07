package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
    List<Product> findByNameContainingIgnoreCase(String name);
}