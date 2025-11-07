package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.Inventory;
import com.smartusers.logitrackapi.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findByInventoryId(Long inventoryId);
}
