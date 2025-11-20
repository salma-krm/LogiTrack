package com.smartusers.logitrackapi.dto.shipment;

import lombok.Data;

@Data
public class ShipmentRequest {
    private String trackingNumber;
    private Long carrierId;
    private Long salesOrderId;
}
