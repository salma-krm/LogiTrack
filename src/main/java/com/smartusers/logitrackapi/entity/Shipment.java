package com.smartusers.logitrackapi.entity;

import com.smartusers.logitrackapi.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String trackingNumber;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;

    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;
    @OneToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;
}
