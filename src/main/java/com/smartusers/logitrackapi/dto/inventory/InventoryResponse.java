package com.smartusers.logitrackapi.dto.inventory;


import com.smartusers.logitrackapi.dto.inventoryMovement.InventoryMovementResponse;
import lombok.Data;
import java.util.List;

@Data
public class InventoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private List<InventoryMovementResponse> movements;
}