package com.smartusers.logitrackapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantityOnHand;
    private Integer quantityReserved;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;


    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryMovement> movements;
}
