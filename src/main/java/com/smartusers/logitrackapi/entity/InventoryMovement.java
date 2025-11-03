package com.smartusers.logitrackapi.entity;

import com.smartusers.logitrackapi.enums.MovementType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MovementType type;

    private Integer quantity;
    private LocalDateTime movementDate;
    private String description;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
}
