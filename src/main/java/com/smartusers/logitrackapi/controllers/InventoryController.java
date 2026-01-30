package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.inventory.*;
import com.smartusers.logitrackapi.entity.Inventory;
import com.smartusers.logitrackapi.mapper.InventoryMapper;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
@CrossOrigin("*")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public InventoryResponse create(@Valid @RequestBody InventoryRequest request) {
        Inventory inventory = inventoryMapper.toEntity(request);
        Inventory created = inventoryService.create(inventory);
        return inventoryMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public InventoryResponse update(@PathVariable Long id, @Valid @RequestBody InventoryRequest request) {
        Inventory inventory = inventoryMapper.toEntity(request);
        Inventory updated = inventoryService.update(id, inventory);
        return inventoryMapper.toResponse(updated);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<InventoryResponse> getAll() {
        return inventoryService.getAll()
                .stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public InventoryResponse getById(@PathVariable Long id) {
        return inventoryMapper.toResponse(inventoryService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        inventoryService.delete(id);
    }
}
