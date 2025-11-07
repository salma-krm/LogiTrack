package com.smartusers.logitrackapi.dto.salesorder;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentSummaryResponse {

    private Long id;
    private String trackingNumber;
    private String carrier;
    private String status;
    private LocalDateTime plannedShipDate;
    private LocalDateTime actualShipDate;

}