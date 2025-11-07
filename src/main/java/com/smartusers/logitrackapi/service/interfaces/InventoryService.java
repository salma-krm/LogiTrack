package com.smartusers.logitrackapi.service.interfaces;

import com.smartusers.logitrackapi.entity.Inventory;

import java.util.List;

public interface InventoryService {

    Inventory create(Inventory inventory);

    Inventory addStock(Long inventoryId, int quantity, String description);

    Inventory sortieStock(Long inventoryId, int quantity, String description);

    Integer checkAvailableByWarehouse(Long productId, Long warehouseId);

    Integer checkAvailableInAllWarehouses(Long productId);

    Inventory update(Long id, Inventory inventory);

    List<Inventory> getAll();

    Inventory getById(Long id);

    void delete(Long id);

}
