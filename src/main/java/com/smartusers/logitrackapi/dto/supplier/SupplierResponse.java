package com.smartusers.logitrackapi.dto.supplier;


import lombok.Data;

@Data
public class SupplierResponse {
    private Long id;
    private String name;
    private String contactInfo;
    private Boolean active;
    private Integer totalPurchaseOrders;
}