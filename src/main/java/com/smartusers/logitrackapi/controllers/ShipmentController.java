package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.shipment.ShipmentRequest;
import com.smartusers.logitrackapi.dto.shipment.ShipmentResponse;
import com.smartusers.logitrackapi.service.interfaces.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;


    @PostMapping
    public ShipmentResponse createShipment(@RequestBody ShipmentRequest request) {
        return shipmentService.create(request);
    }


    @PutMapping("/{id}/ship")
    public ShipmentResponse markAsShipped(@PathVariable Long id) {
        return shipmentService.markAsShipped(id);
    }


    @PutMapping("/{id}/deliver")
    public ShipmentResponse markAsDelivered(@PathVariable Long id) {
        return shipmentService.markAsDelivered(id);
    }


    @GetMapping("/{id}")
    public ShipmentResponse getShipmentById(@PathVariable Long id) {
        return shipmentService.getById(id);
    }


    @GetMapping
    public List<ShipmentResponse> getAllShipments() {
        return shipmentService.getAll();
    }


    @DeleteMapping("/{id}")
    public void deleteShipment(@PathVariable Long id) {
        shipmentService.delete(id);
    }
}
