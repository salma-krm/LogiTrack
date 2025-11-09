package com.smartusers.logitrackapi.dto.purchaseorder;



import lombok.Data;

@Data
public class PurchaseOrderLineResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantityOrdered;
    private Integer quantityReceived;
    private Double unitPrice;
    private Double totalPrice;
}