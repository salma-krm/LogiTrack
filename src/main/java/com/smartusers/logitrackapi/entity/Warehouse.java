package com.smartusers.logitrackapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private Boolean active = true;
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Inventory> inventories;
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;
}
