package com.smartusers.logitrackapi.dto.inventoryMovement;

import com.smartusers.logitrackapi.enums.MovementType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryMovementResponse {
    private Long id;
    private MovementType type;
    private Integer quantity;
    private String description;
    private LocalDateTime movementDate;
}