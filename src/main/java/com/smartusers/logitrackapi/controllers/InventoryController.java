package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.inventory.*;
import com.smartusers.logitrackapi.entity.Inventory;
import com.smartusers.logitrackapi.mapper.InventoryMapper;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;



import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    @PostMapping
    public InventoryResponse create(@Valid @RequestBody InventoryRequest request) {
        Inventory inventory = inventoryMapper.toEntity(request);
        Inventory created = inventoryService.create(inventory);
        return inventoryMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public InventoryResponse update(@PathVariable Long id, @Valid @RequestBody InventoryRequest request) {
        Inventory inventory = inventoryMapper.toEntity(request);
        Inventory updated = inventoryService.update(id, inventory);
        return inventoryMapper.toResponse(updated);
    }

    @GetMapping
    public List<InventoryResponse> getAll() {
        return inventoryService.getAll()
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public InventoryResponse getById(@PathVariable Long id) {
        return inventoryMapper.toResponse(inventoryService.getById(id));
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inventoryService.delete(id);
    }
}
