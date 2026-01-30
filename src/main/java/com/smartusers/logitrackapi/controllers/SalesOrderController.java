package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.salesorder.SalesOrderRequest;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderResponse;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.mapper.SalesOrderMapper;
import com.smartusers.logitrackapi.service.interfaces.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;
    private final SalesOrderMapper salesOrderMapper;

    // ➕ Créer une commande
    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or " +
                  "(hasRole('CLIENT') and @ownershipChecker.isOrderOwner(#id, authentication))")
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
    @PreAuthorize("hasRole('ADMIN') or " +
                  "(hasRole('CLIENT') and @ownershipChecker.isOrderOwner(#id, authentication))")
    public ResponseEntity<SalesOrderResponse> cancelOrder(@PathVariable Long id) {
        SalesOrder canceled = salesOrderService.cancel(id);
        return ResponseEntity.ok(salesOrderMapper.toResponse(canceled));
    }

    // 🚚 Marquer comme expédiée
    @PostMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesOrderResponse> shipOrder(@PathVariable Long id) {
        SalesOrder shipped = salesOrderService.markAsShipped(id);
        return ResponseEntity.ok(salesOrderMapper.toResponse(shipped));
    }

    // 📦 Marquer comme livrée
    @PostMapping("/{id}/deliver")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesOrderResponse> deliverOrder(@PathVariable Long id) {
        SalesOrder delivered = salesOrderService.markAsDelivered(id);
        return ResponseEntity.ok(salesOrderMapper.toResponse(delivered));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CLIENT')")
    public ResponseEntity<Page<SalesOrderResponse>> getAll(Pageable pageable) {
        // Note: Pour CLIENT, filtrer par client dans le service
        Page<SalesOrderResponse> orders = salesOrderService.getAll(pageable)
                .map(salesOrderMapper::toResponse);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or " +
                  "(hasRole('CLIENT') and @ownershipChecker.isOrderOwner(#id, authentication))")
    public ResponseEntity<SalesOrderResponse> getById(@PathVariable Long id) {
        SalesOrder order = salesOrderService.getById(id);
        return ResponseEntity.ok(salesOrderMapper.toResponse(order));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<SalesOrderResponse> update(@PathVariable Long id, @Valid @RequestBody SalesOrderRequest request) {
        SalesOrder updated = salesOrderService.update(id, request);
        return ResponseEntity.ok(salesOrderMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        salesOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
