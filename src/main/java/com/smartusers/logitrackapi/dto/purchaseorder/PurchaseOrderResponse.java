package com.smartusers.logitrackapi.dto.purchaseorder;


import com.smartusers.logitrackapi.enums.POStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderResponse {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private POStatus status;
    private LocalDateTime createdAt;
    private Double totalAmount;
    private List<PurchaseOrderLineResponse> lines;
}