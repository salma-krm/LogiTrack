package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.supplier.SupplierRequest;
import com.smartusers.logitrackapi.dto.supplier.SupplierResponse;
import com.smartusers.logitrackapi.entity.Supplier;
import com.smartusers.logitrackapi.mapper.SupplierMapper;
import com.smartusers.logitrackapi.service.interfaces.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suppliers")
@CrossOrigin("*")
public class SupplierController {

    private final SupplierService supplierService;
    private final SupplierMapper supplierMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SupplierResponse create(@Valid @RequestBody SupplierRequest request) {
        Supplier supplier = supplierService.create(request);
        return supplierMapper.toResponse(supplier);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SupplierResponse update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        Supplier supplier = supplierService.update(id, request);
        return supplierMapper.toResponse(supplier);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<SupplierResponse> getAll() {
        return supplierService.getAll()
                .stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public SupplierResponse getById(@PathVariable Long id) {
        Supplier supplier = supplierService.getById(id);
        return supplierMapper.toResponse(supplier);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        supplierService.delete(id);
    }
}
