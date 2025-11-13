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


    PurchaseOrder approve(Long id);
    PurchaseOrder cancel(Long id);
    PurchaseOrder receive(Long id, Long warehouseId);

    void delete(Long id);
}
