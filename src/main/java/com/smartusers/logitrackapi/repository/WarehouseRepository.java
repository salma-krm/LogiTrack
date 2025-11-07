package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByName(String name);
}
