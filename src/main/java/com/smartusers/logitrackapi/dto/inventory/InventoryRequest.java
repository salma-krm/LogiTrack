package com.smartusers.logitrackapi.dto.inventory;

import com.smartusers.logitrackapi.dto.inventoryMovement.InventoryMovementRequest;
import lombok.Data;
import java.util.List;

@Data
public class InventoryRequest {
    private Long productId;
    private Long warehouseId;
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private List<InventoryMovementRequest> movements;
}