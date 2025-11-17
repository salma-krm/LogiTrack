package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.salesorder.SalesOrderRequest;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderResponse;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.enums.OrderStatus;
import com.smartusers.logitrackapi.mapper.SalesOrderMapper;
import com.smartusers.logitrackapi.service.interfaces.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;
    private final SalesOrderMapper salesOrderMapper;

    // ➕ Créer une commande
    @PostMapping
    public ResponseEntity<SalesOrderResponse> create(@Valid @RequestBody SalesOrderRequest request) {
        try {
            SalesOrder created = salesOrderService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(salesOrderMapper.toResponse(created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ✅ Confirmer une commande (réservation du stock)
    @PostMapping("/{id}/confirm")
    public ResponseEntity<SalesOrderResponse> confirmOrder(@PathVariable Long id) {
        try {
            SalesOrder confirmedOrder = salesOrderService.confirmerOrderByClient(id);
            return ResponseEntity.ok(salesOrderMapper.toResponse(confirmedOrder));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ❌ Annuler une commande
    @PostMapping("/{id}/cancel")
    public ResponseEntity<SalesOrderResponse> cancelOrder(@PathVariable Long id) {
        SalesOrder canceled = salesOrderService.cancel(id);
        return ResponseEntity.ok(salesOrderMapper.toResponse(canceled));
    }

    // 🚚 Marquer comme expédiée
    @PostMapping("/{id}/ship")
    public ResponseEntity<SalesOrderResponse> shipOrder(@PathVariable Long id) {
        SalesOrder shipped = salesOrderService.markAsShipped(id);
        return ResponseEntity.ok(salesOrderMapper.toResponse(shipped));
    }

    // 📦 Marquer comme livrée
    @PostMapping("/{id}/deliver")
    public ResponseEntity<SalesOrderResponse> deliverOrder(@PathVariable Long id) {
        SalesOrder delivered = salesOrderService.markAsDelivered(id);
        return ResponseEntity.ok(salesOrderMapper.toResponse(delivered));
    }
}
