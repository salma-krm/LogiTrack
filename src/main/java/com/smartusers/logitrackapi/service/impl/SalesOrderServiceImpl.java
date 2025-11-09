package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.dto.salesorder.SalesOrderRequest;
import com.smartusers.logitrackapi.entity.*;
import com.smartusers.logitrackapi.enums.OrderStatus;
import com.smartusers.logitrackapi.enums.SalesOrderStatus;
import com.smartusers.logitrackapi.repository.*;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import com.smartusers.logitrackapi.service.interfaces.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    @Override

    @Transactional
    public SalesOrder create(SalesOrderRequest request) {
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé"));

        // Create the order WITHOUT lines first
        SalesOrder order = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .lines(new ArrayList<>())  // Initialize empty list
                .build();

        // Save and flush to get the ID
        order = salesOrderRepository.saveAndFlush(order);

        // NOW add the lines
        for (var lineReq : request.getOrderLines()) {
            Product product = productRepository.findById(lineReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            SalesOrderLine line = SalesOrderLine.builder()
                    .salesOrder(order)
                    .product(product)
                    .qtyOrdered(lineReq.getQuantity())
                    .qtyReserved(0)
                    .price(lineReq.getUnitPrice())
                    .build();

            order.getLines().add(line);
        }

        // Save again with the lines
        return salesOrderRepository.save(order);
    }





    @Override
    public SalesOrder confirmerOrderByClient(SalesOrder order) {
        SalesOrderLine salesOrderLine = salesOrderLineRepository.findFirstBySalesOrder_Id(order.getId());
        Inventory salesOrder = inventoryRepository.findInventoryByWarehouseAndProduct(order.getWarehouse().getId(),salesOrderLine.getProduct().getId());
        boolean allReserved = true;
        List<SalesOrderLine> lines = salesOrderLineRepository.findBySalesOrder_Id(order.getId());
        for (SalesOrderLine line : lines) {
            int needed = line.getQtyOrdered();
            int reserved = 0;
            int available = inventoryService.checkAvailableByWarehouse(line.getProduct().getId(), order.getWarehouse().getId());
            int toReserve = Math.min(available, needed);
            if (toReserve > 0) {
                Inventory inv = inventoryRepository.findInventoryByWarehouseAndProduct(order.getWarehouse().getId(), line.getProduct().getId());
                inv.setQuantityReserved(inv.getQuantityReserved() + toReserve);
                inventoryRepository.save(inv);
                inventoryService.sortieStock(inv.getId(), 0, "Réservation " + toReserve);
                reserved += toReserve;
            }


            if (reserved < needed) {
                List<Inventory> otherInvs = inventoryRepository.findAllAvailableByProduct(line.getProduct().getId());
                for (Inventory inv : otherInvs) {
                    if (inv.getWarehouse().getId().equals(order.getWarehouse().getId())) continue;
                    int avail = inv.getQuantityOnHand() - inv.getQuantityReserved();
                    int reserveQty = Math.min(avail, needed - reserved);
                    if (reserveQty > 0) {
                        inv.setQuantityReserved(inv.getQuantityReserved() + reserveQty);
                        inventoryRepository.save(inv);
                        inventoryService.sortieStock(inv.getId(), 0, "Réservation " + reserveQty);
                        reserved += reserveQty;
                    }
                    if (reserved >= needed) break;
                }
            }

            if (reserved < needed) allReserved = false;

            line.setQtyReserved(reserved);
            salesOrderLineRepository.save(line);
        }


        if (allReserved) order.setStatus(OrderStatus.RESERVED);
        else order.setStatus(OrderStatus.CANCELED);

        return salesOrderRepository.save(order);
    }

    @Override
    public SalesOrder update(Long id, SalesOrderRequest request) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order non trouvé"));
        return salesOrderRepository.save(order);
    }

    @Override
    public SalesOrder getById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order non trouvé"));
    }

//    @Override
//    public SalesOrder getByOrderNumber(String orderNumber) {
//        return salesOrderRepository.findByOrderNumber(orderNumber)
//                .orElseThrow(() -> new RuntimeException("Order non trouvé"));
//    }


    @Override
    public Page<SalesOrder> getAll(Pageable pageable) {
        return salesOrderRepository.findAll(pageable);
    }

    @Override
    public Page<SalesOrder> getByClientId(Long clientId, Pageable pageable) {
        return salesOrderRepository.findByClient_Id(clientId, pageable);
    }

    @Override
    public Page<SalesOrder> getByStatus(SalesOrderStatus status, Pageable pageable) {
        return salesOrderRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<SalesOrder> getByClientIdAndStatus(Long clientId, SalesOrderStatus status, Pageable pageable) {
        return salesOrderRepository.findByClient_IdAndStatus(clientId, status, pageable);
    }

    @Override
    public SalesOrder cancel(Long id) {
        SalesOrder order = getById(id);
        order.setStatus(OrderStatus.CANCELED);
        return salesOrderRepository.save(order);
    }

    @Override
    public SalesOrder markAsShipped(Long id) {
        SalesOrder order = getById(id);
        order.setStatus(OrderStatus.SHIPPED);
        return salesOrderRepository.save(order);
    }

    @Override
    public SalesOrder markAsDelivered(Long id) {
        SalesOrder order = getById(id);
        order.setStatus(OrderStatus.DELIVERED);
        return salesOrderRepository.save(order);
    }

    @Override
    public int releaseExpiredReservations(int expiryDuration) {
        // يمكن إضافة لوجيك حسب مدة انتهاء الحجز
        return 0;
    }

    @Override
    public List<SalesOrder> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return salesOrderRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public void delete(Long id) {
        salesOrderRepository.deleteById(id);
    }
}
