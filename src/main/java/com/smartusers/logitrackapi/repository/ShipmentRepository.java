package com.smartusers.logitrackapi.repository;


import com.smartusers.logitrackapi.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}
