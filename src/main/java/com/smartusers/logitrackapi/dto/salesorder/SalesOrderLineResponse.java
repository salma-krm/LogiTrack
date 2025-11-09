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
    private int qtyOrdered;
    private int qtyReserved;
    private BigDecimal price;
}
