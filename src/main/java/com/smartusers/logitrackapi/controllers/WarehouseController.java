package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.warehouse.WarehouseRequest;
import com.smartusers.logitrackapi.dto.warehouse.WarehouseResponse;
import com.smartusers.logitrackapi.entity.Warehouse;
import com.smartusers.logitrackapi.mapper.WarehouseMapper;
import com.smartusers.logitrackapi.service.interfaces.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warehouses")
@CrossOrigin("*")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final WarehouseMapper warehouseMapper;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public WarehouseResponse create(@Valid @RequestBody WarehouseRequest request) {
        Warehouse warehouse = warehouseMapper.toEntity(request);
        Warehouse created = warehouseService.create(warehouse);
        return warehouseMapper.toResponse(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CLIENT')")
    public List<WarehouseResponse> getAll() {
        return warehouseService.getAll().stream()
                .map(warehouseMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CLIENT')")
    public WarehouseResponse getById(@PathVariable Long id) {
        return warehouseMapper.toResponse(warehouseService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public WarehouseResponse update(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request
    ) {
        Warehouse warehouse = warehouseMapper.toEntity(request);
        Warehouse updated = warehouseService.update(id, warehouse);
        return warehouseMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        warehouseService.delete(id);
    }
}
