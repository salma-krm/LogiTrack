package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.enums.SalesOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {


//    Optional<SalesOrder> findByOrderNumber(String orderNumber);
   Optional<SalesOrder> findById(Long salesOrderId);
    Page<SalesOrder> findByClient_Id(Long clientId, Pageable pageable);

    // 🔹 Rechercher toutes les commandes d’un statut donné (avec pagination)
    Page<SalesOrder> findByStatus(SalesOrderStatus status, Pageable pageable);

    // 🔹 Rechercher les commandes d’un client avec un statut donné
    Page<SalesOrder> findByClient_IdAndStatus(Long clientId, SalesOrderStatus status, Pageable pageable);

    // 🔹 Rechercher les commandes créées entre deux dates
    List<SalesOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);;
}