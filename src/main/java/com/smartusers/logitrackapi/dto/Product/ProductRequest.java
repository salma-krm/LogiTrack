package com.smartusers.logitrackapi.dto.Product;



import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String sku;
    private String name;
    private Long categoryId;
    private BigDecimal originalPrice;
    private BigDecimal profit;
    private String unit;
    private Boolean active;
}
