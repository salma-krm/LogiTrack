package com.smartusers.logitrackapi.dto.salesorder;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderLineResponse {

    private Long id;
    private Long productId;
    private String productSku;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private Integer quantityReserved;
    private Integer quantityShipped;
    private Integer pendingQuantity;
}