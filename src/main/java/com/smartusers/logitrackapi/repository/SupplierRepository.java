package com.smartusers.logitrackapi.repository;


import com.smartusers.logitrackapi.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByName(String name);
    List<Supplier> findByActiveTrue();
}