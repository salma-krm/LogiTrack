package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.supplier.SupplierRequest;
import com.smartusers.logitrackapi.dto.supplier.SupplierResponse;
import com.smartusers.logitrackapi.entity.Supplier;
import com.smartusers.logitrackapi.mapper.SupplierMapper;
import com.smartusers.logitrackapi.service.interfaces.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;
    private final SupplierMapper supplierMapper;

    @PostMapping
    public SupplierResponse create(@Valid @RequestBody SupplierRequest request) {
        Supplier supplier = supplierService.create(request);
        return supplierMapper.toResponse(supplier);
    }

    @PutMapping("/{id}")
    public SupplierResponse update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        Supplier supplier = supplierService.update(id, request);
        return supplierMapper.toResponse(supplier);
    }

    @GetMapping
    public List<SupplierResponse> getAll() {
        return supplierService.getAll()
                .stream()
                .map(supplierMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public SupplierResponse getById(@PathVariable Long id) {
        Supplier supplier = supplierService.getById(id);
        return supplierMapper.toResponse(supplier);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        supplierService.delete(id);
    }

    @PatchMapping("/{id}/activate")
    public SupplierResponse activate(@PathVariable Long id) {
        Supplier supplier = supplierService.activate(id);
        return supplierMapper.toResponse(supplier);
    }

    @PatchMapping("/{id}/deactivate")
    public SupplierResponse deactivate(@PathVariable Long id) {
        Supplier supplier = supplierService.deactivate(id);
        return supplierMapper.toResponse(supplier);
    }
}
