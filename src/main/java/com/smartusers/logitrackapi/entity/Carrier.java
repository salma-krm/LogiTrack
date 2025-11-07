package com.smartusers.logitrackapi.entity;

import com.smartusers.logitrackapi.enums.CarrierStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String contactEmail;
    private String contactPhone;
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private CarrierStatus status;

    @OneToMany(mappedBy = "carrier")
    private List<Shipment> shipments;
}
