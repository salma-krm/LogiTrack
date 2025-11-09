package com.smartusers.logitrackapi.repository;


import com.smartusers.logitrackapi.entity.POLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface POLineRepository extends JpaRepository<POLine, Long> {
    List<POLine> findByPurchaseOrder_Id(Long purchaseOrderId);
}