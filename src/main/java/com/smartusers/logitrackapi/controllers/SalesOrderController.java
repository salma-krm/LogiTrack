package com.smartusers.logitrackapi.controllers;
import com.smartusers.logitrackapi.annotation.RequireAuth;
import com.smartusers.logitrackapi.annotation.RequireRole;
import com.smartusers.logitrackapi.dto.inventory.InventoryRequest;
import com.smartusers.logitrackapi.dto.inventory.InventoryResponse;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderRequest;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderResponse;
import com.smartusers.logitrackapi.entity.Inventory;
import com.smartusers.logitrackapi.entity.Product;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.mapper.InventoryMapper;
import com.smartusers.logitrackapi.mapper.SalesOrderMapper;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import com.smartusers.logitrackapi.service.interfaces.ProductService;
import com.smartusers.logitrackapi.service.interfaces.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/SalesOrders")
@RequiredArgsConstructor
@CrossOrigin("*")

public class SalesOrderController {
    private final SalesOrderService salesOrderService;
    private final SalesOrderMapper salesOrderMapper;
    @PostMapping
    public SalesOrderResponse create(@Valid @RequestBody SalesOrderRequest request) {
        SalesOrder created = salesOrderService.create(request);
        return salesOrderMapper.toResponse(created);
    }

}
