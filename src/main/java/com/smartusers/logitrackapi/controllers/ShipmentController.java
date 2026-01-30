package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.shipment.ShipmentRequest;
import com.smartusers.logitrackapi.dto.shipment.ShipmentResponse;
import com.smartusers.logitrackapi.service.interfaces.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ShipmentResponse createShipment(@RequestBody ShipmentRequest request) {
        return shipmentService.create(request);
    }

    @PutMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ShipmentResponse markAsShipped(@PathVariable Long id) {
        return shipmentService.markAsShipped(id);
    }

    @PutMapping("/{id}/deliver")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ShipmentResponse markAsDelivered(@PathVariable Long id) {
        return shipmentService.markAsDelivered(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or " +
                  "(hasRole('CLIENT') and @ownershipChecker.isShipmentOwner(#id, authentication))")
    public ShipmentResponse getShipmentById(@PathVariable Long id) {
        return shipmentService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('CLIENT')")
    public List<ShipmentResponse> getAllShipments() {
        // Note: Pour CLIENT, filtrer par client dans le service
        return shipmentService.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteShipment(@PathVariable Long id) {
        shipmentService.delete(id);
    }
}
