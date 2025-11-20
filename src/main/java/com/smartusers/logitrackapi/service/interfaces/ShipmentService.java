package com.smartusers.logitrackapi.service.interfaces;

import com.smartusers.logitrackapi.dto.shipment.ShipmentRequest;
import com.smartusers.logitrackapi.dto.shipment.ShipmentResponse;

import java.util.List;

public interface ShipmentService {

    ShipmentResponse create(ShipmentRequest request);

    ShipmentResponse markAsShipped(Long id);

    ShipmentResponse markAsDelivered(Long id);

    ShipmentResponse getById(Long id);

    List<ShipmentResponse> getAll();

    void delete(Long id);
}
