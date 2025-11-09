package com.smartusers.logitrackapi.repository;


import com.smartusers.logitrackapi.entity.PurchaseOrder;
import com.smartusers.logitrackapi.enums.POStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Page<PurchaseOrder> findBySupplier_Id(Long supplierId, Pageable pageable);
    Page<PurchaseOrder> findByStatus(POStatus status, Pageable pageable);
    List<PurchaseOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
