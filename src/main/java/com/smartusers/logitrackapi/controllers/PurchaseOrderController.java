package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderRequest;
import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderResponse;
import com.smartusers.logitrackapi.entity.PurchaseOrder;
import com.smartusers.logitrackapi.enums.POStatus;
import com.smartusers.logitrackapi.mapper.PurchaseOrderMapper;
import com.smartusers.logitrackapi.service.interfaces.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @PostMapping
    public PurchaseOrderResponse create(@Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrder po = purchaseOrderService.create(request);
        return purchaseOrderMapper.toResponse(po);
    }

    @PutMapping("/{id}")
    public PurchaseOrderResponse update(@PathVariable Long id, @Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrder po = purchaseOrderService.update(id, request);
        return purchaseOrderMapper.toResponse(po);
    }

    @GetMapping
    public List<PurchaseOrderResponse> getAll() {
        return purchaseOrderService.getAll()
                .stream()
                .map(purchaseOrderMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PurchaseOrderResponse getById(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderService.getById(id);
        return purchaseOrderMapper.toResponse(po);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
    }

    @PatchMapping("/{id}/approve")
    public PurchaseOrderResponse approve(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderService.approve(id);
        return purchaseOrderMapper.toResponse(po);
    }

    @PatchMapping("/{id}/receive")
    public PurchaseOrderResponse receive(@PathVariable Long id, @RequestParam Long warehouseId) {
        PurchaseOrder po = purchaseOrderService.receive(id, warehouseId);
        return purchaseOrderMapper.toResponse(po);
    }

    @PatchMapping("/{id}/cancel")
    public PurchaseOrderResponse cancel(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderService.cancel(id);
        return purchaseOrderMapper.toResponse(po);
    }



    @GetMapping("/supplier/{supplierId}")
    public Page<PurchaseOrderResponse> getBySupplierId(@PathVariable Long supplierId, Pageable pageable) {
        return purchaseOrderService.getBySupplierId(supplierId, pageable)
                .map(purchaseOrderMapper::toResponse);
    }


}
