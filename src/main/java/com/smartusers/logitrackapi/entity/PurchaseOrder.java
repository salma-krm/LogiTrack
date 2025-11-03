package com.smartusers.logitrackapi.entity;

import com.smartusers.logitrackapi.enums.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime expectedDelivery;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;



}
