package com.smartusers.logitrackapi.service.interfaces;

import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderRequest;
import com.smartusers.logitrackapi.entity.PurchaseOrder;
import com.smartusers.logitrackapi.enums.POStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrder create(PurchaseOrderRequest request);
    PurchaseOrder update(Long id, PurchaseOrderRequest request);
    PurchaseOrder getById(Long id);
    List<PurchaseOrder> getAll();
    Page<PurchaseOrder> getAll(Pageable pageable);
    Page<PurchaseOrder> getBySupplierId(Long supplierId, Pageable pageable);
    Page<PurchaseOrder> getByStatus(POStatus status, Pageable pageable);
    PurchaseOrder changeStatus(Long id, POStatus status);
    PurchaseOrder approve(Long id);
    PurchaseOrder receive(Long id);
    PurchaseOrder cancel(Long id);
    List<PurchaseOrder> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    void delete(Long id);
}
