package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.enums.SalesOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    // EntityGraph : charger client, warehouse, lines, lines.product
    @EntityGraph(attributePaths = {"lines", "lines.product", "client", "warehouse"})
    Optional<SalesOrder> findWithDetailsById(Long id);

    // Autres méthodes...
    Page<SalesOrder> findByClient_Id(Long clientId, Pageable pageable);
    Page<SalesOrder> findByStatus(SalesOrderStatus status, Pageable pageable);
    Page<SalesOrder> findByClient_IdAndStatus(Long clientId, SalesOrderStatus status, Pageable pageable);
    List<SalesOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<SalesOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
