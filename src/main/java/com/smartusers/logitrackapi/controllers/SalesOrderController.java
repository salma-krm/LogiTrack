package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.salesorder.SalesOrderRequest;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderResponse;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.enums.SalesOrderStatus;
import com.smartusers.logitrackapi.mapper.SalesOrderMapper;
import com.smartusers.logitrackapi.service.interfaces.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<SalesOrderResponse> confirmOrder(@PathVariable Long id) {
        try {

            SalesOrder order = salesOrderService.getById(id);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }


            SalesOrder confirmedOrder = salesOrderService.confirmerOrderByClient(order);

            SalesOrderResponse response = SalesOrderResponse.builder()
                    .id(confirmedOrder.getId())
                    .status(SalesOrderStatus.valueOf(confirmedOrder.getStatus().name()))
                    .clientId(confirmedOrder.getClient().getId())
                    .warehouseId(confirmedOrder.getWarehouse().getId())
                    .createdAt(confirmedOrder.getCreatedAt())
                    .build();



            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
