package com.smartusers.logitrackapi.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="sales_orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private com.smartusers.logitrackapi.enums.OrderStatus status = com.smartusers.logitrackapi.enums.OrderStatus.CREATED;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehous_id", nullable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<SalesOrderLine> lines = new ArrayList<>();

    @OneToOne(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Shipment shipment;

}
