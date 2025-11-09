package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.salesorder.SalesOrderRequest;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderResponse;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.mapper.SalesOrderMapper;
import com.smartusers.logitrackapi.service.interfaces.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales-orders") // ✅ en minuscules
@RequiredArgsConstructor

public class SalesOrderController {

    private final SalesOrderService salesOrderService;
    private final SalesOrderMapper salesOrderMapper;

    @PostMapping
    public SalesOrderResponse create(@Valid @RequestBody SalesOrderRequest request) {
        SalesOrder created = salesOrderService.create(request);
        return salesOrderMapper.toResponse(created);
    }
    // Confirmer une commande
    @PostMapping("/{id}/confirm")
    public ResponseEntity<SalesOrder> confirm(@PathVariable Long id) {
        SalesOrder order = salesOrderService.getById(id);
        System.out.println(order);
        order = salesOrderService.confirmerOrderByClient(order);

        return ResponseEntity.ok(order);
    }
}
