package com.smartusers.logitrackapi.service.interfaces;

import com.smartusers.logitrackapi.dto.salesorder.SalesOrderRequest;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.enums.SalesOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesOrderService {

    SalesOrder create(SalesOrderRequest request);


    SalesOrder confirmerOrderByClient(Long orderId);
    SalesOrder update(Long id, SalesOrderRequest request);

    SalesOrder getById(Long id);


    SalesOrder cancel(Long id);

    SalesOrder markAsShipped(Long id);

    SalesOrder markAsDelivered(Long id);

   Page<SalesOrder> getAll(Pageable pageable);

    void delete(Long id);
}
