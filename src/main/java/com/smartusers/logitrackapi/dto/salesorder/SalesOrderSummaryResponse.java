package com.smartusers.logitrackapi.dto.salesorder;

import com.smartusers.logitrackapi.enums.SalesOrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderSummaryResponse {

    private Long id;
    private String orderNumber;
    private String clientName;
    private String warehouseCode;
    private SalesOrderStatus status;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private Integer totalItems;
}