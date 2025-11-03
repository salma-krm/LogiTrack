package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}