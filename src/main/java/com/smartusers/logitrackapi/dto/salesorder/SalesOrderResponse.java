package com.smartusers.logitrackapi.dto.salesorder;

import com.smartusers.logitrackapi.enums.SalesOrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderResponse {

    private Long id;
    private String orderNumber;
    private Long clientId;
    private String clientName;
    private Long warehouseId;
    private String warehouseCode;
    private SalesOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reservedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime canceledAt;
    private BigDecimal totalAmount;
    private String notes;
    private List<SalesOrderLineResponse> orderLines;
    private ShipmentSummaryResponse shipment;
}