package com.smartusers.logitrackapi.dto.shipment;

import com.smartusers.logitrackapi.enums.ShipmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentResponse {
    private Long id;
    private String trackingNumber;
    private ShipmentStatus status;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
    private Long carrierId;
    private Long salesOrderId;
}
