package com.smartusers.logitrackapi.dto.Product;



import com.smartusers.logitrackapi.entity.Inventory;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal originalPrice;
    private BigDecimal profit;
    private String unit;
    private Boolean active;
    private Long categoryId;
}