package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.annotation.RequireAuth;
import com.smartusers.logitrackapi.annotation.RequireRole;
import com.smartusers.logitrackapi.dto.warehouse.WarehouseRequest;
import com.smartusers.logitrackapi.dto.warehouse.WarehouseResponse;
import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.entity.Warehouse;
import com.smartusers.logitrackapi.mapper.WarehouseMapper;
import com.smartusers.logitrackapi.service.impl.SessionManager;
import com.smartusers.logitrackapi.service.interfaces.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final WarehouseMapper warehouseMapper;
    private final SessionManager sessionManager;

    @RequireAuth
    @RequireRole("MANAGER")
    @PostMapping
    public WarehouseResponse create(
            @RequestHeader("Session-Id") String sessionId,
            @Valid @RequestBody WarehouseRequest request
    ) {
        Warehouse warehouse = warehouseMapper.toEntity(request);

        User manager = sessionManager.getUserBySessionId(sessionId);
        warehouse.setManager(manager);

        Warehouse created = warehouseService.create(warehouse);
        return warehouseMapper.toResponse(created);
    }

    @RequireAuth
    @RequireRole("MANAGER")
    @GetMapping
    public List<WarehouseResponse> getAll() {
        return warehouseService.getAll().stream()
                .map(warehouseMapper::toResponse)
                .toList();
    }

    @RequireAuth
    @RequireRole("MANAGER")
    @GetMapping("/{id}")
    public WarehouseResponse getById(@PathVariable Long id) {
        return warehouseMapper.toResponse(warehouseService.getById(id));
    }

    @RequireAuth
    @RequireRole("MANAGER")
    @PutMapping("/{id}")
    public WarehouseResponse update(
            @RequestHeader("Session-Id") String sessionId,
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request
    ) {
        Warehouse warehouse = warehouseMapper.toEntity(request);

        User manager = sessionManager.getUserBySessionId(sessionId);
        warehouse.setManager(manager);

        Warehouse updated = warehouseService.update(id, warehouse);
        return warehouseMapper.toResponse(updated);
    }

    @RequireAuth
    @RequireRole("MANAGER")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        warehouseService.delete(id);
    }
}
