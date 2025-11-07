package com.smartusers.logitrackapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String country;
    private String city;
    private String street;
    private String zipcode;

}
