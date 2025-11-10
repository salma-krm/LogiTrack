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
    private Long clientId;
    private String clientName;
    private Long warehouseId;
    private SalesOrderStatus status;
    private LocalDateTime createdAt;
    private String notes;
    private List<SalesOrderLineResponse> orderLines;
}