package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.Exception.ResourceNotFoundException;
import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderLineRequest;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderLineRequest;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderRequest;
import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderRequest;
import com.smartusers.logitrackapi.entity.*;
import com.smartusers.logitrackapi.enums.OrderStatus;
import com.smartusers.logitrackapi.repository.*;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import com.smartusers.logitrackapi.service.interfaces.PurchaseOrderService;
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
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    private final PurchaseOrderService purchaseOrderService;
    private final SupplierRepository supplierRepository;

    // -------------------- Création d'une commande client --------------------
    @Override
    public SalesOrder create(SalesOrderRequest request) {
        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new BusinessException("Client non trouvé"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new BusinessException("Entrepôt non trouvé"));

        SalesOrder order = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .lines(new ArrayList<>())
                .build();

        order = salesOrderRepository.saveAndFlush(order);

        for (SalesOrderLineRequest lineReq : request.getOrderLines()) {
            Product product = productRepository.findById(lineReq.getProductId())
                    .orElseThrow(() -> new BusinessException("Produit non trouvé"));

            SalesOrderLine line = SalesOrderLine.builder()
                    .salesOrder(order)
                    .product(product)
                    .qtyOrdered(lineReq.getQuantity())
                    .qtyReserved(0)
                    .price(lineReq.getUnitPrice())
                    .build();

            salesOrderLineRepository.save(line);
            order.getLines().add(line);
        }

        return salesOrderRepository.save(order);
    }

    // -------------------- Confirmer une commande --------------------

    @Override
    @Transactional
    public SalesOrder confirmerOrderByClient(Long orderId) {
        SalesOrder order = salesOrderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new BusinessException("Commande non trouvée: " + orderId));

        boolean allReserved = true;

        for (SalesOrderLine line : order.getLines()) {
            int needed = line.getQtyOrdered();
            int totalReserved = 0;
            Long productId = line.getProduct().getId();


            Inventory mainInventory = inventoryRepository
                    .findAllByWarehouse_IdAndProduct_Id(order.getWarehouse().getId(), productId)
                    .stream().findFirst().orElse(null);

            if (mainInventory != null) {
                int available = mainInventory.getQuantityOnHand() - mainInventory.getQuantityReserved();
                int toReserve = Math.min(needed, available);
                if (toReserve > 0) {
                    inventoryService.sortieStock(mainInventory.getId(), toReserve,
                            "Réservation commande " + order.getId() + " - produit " + line.getProduct().getName());
                    totalReserved += toReserve;
                }
            }


            if (totalReserved < needed) {
                List<Inventory> otherInventories = inventoryRepository.findAllByProduct_Id(productId).stream()
                        .filter(inv -> inv.getWarehouse().getActive() && !inv.getWarehouse().getId().equals(order.getWarehouse().getId()))
                        .toList();

                for (Inventory inv : otherInventories) {
                    int available = inv.getQuantityOnHand() - inv.getQuantityReserved();
                    int toReserve = Math.min(needed - totalReserved, available);
                    if (toReserve > 0) {
                        inventoryService.sortieStock(inv.getId(), toReserve,
                                "Réservation commande " + order.getId() + " - produit " + line.getProduct().getName());
                        totalReserved += toReserve;
                        if (totalReserved >= needed) break;
                    }
                }
            }


            if (totalReserved < needed) {
                allReserved = false;
                int missingQty = needed - totalReserved;

                Supplier defaultSupplier = supplierRepository.findByActiveTrue().stream()
                        .findFirst()
                        .orElseThrow(() -> new BusinessException("Aucun fournisseur actif trouvé"));

                PurchaseOrderRequest poRequest = new PurchaseOrderRequest();
                poRequest.setSupplierId(defaultSupplier.getId());
                poRequest.setOrderLines(List.of(
                        new PurchaseOrderLineRequest(
                                line.getProduct().getId(),
                                missingQty,
                                line.getPrice()
                        )
                ));
                purchaseOrderService.create(poRequest);


                if (mainInventory == null) {
                    mainInventory = new Inventory();
                    mainInventory.setProduct(line.getProduct());
                    mainInventory.setWarehouse(order.getWarehouse());
                    mainInventory.setQuantityOnHand(0);
                    mainInventory.setQuantityReserved(0);
                    mainInventory = inventoryRepository.save(mainInventory);
                }

                inventoryService.addStock(mainInventory.getId(), missingQty,
                        "Réception suite au Purchase Order automatique");
                inventoryService.sortieStock(mainInventory.getId(), missingQty,
                        "Réservation complémentaire après réapprovisionnement");

                totalReserved += missingQty;
            }

            line.setQtyReserved(totalReserved);
        }

        order.setStatus(OrderStatus.RESERVED);
        salesOrderRepository.save(order);

        String reservationMessage = allReserved
                ? "La réservation a été effectuée complètement."
                : "La réservation est partielle. Un Purchase Order a été créé pour compléter le stock.";

        System.out.println(reservationMessage);

        return order;
    }


    // -------------------- Autres méthodes --------------------
    @Override
    public SalesOrder update(Long id, SalesOrderRequest request) {
        throw new UnsupportedOperationException("Update non encore implémenté");
    }

    @Override
    public SalesOrder getById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'id: " + id));
    }

    @Override
    public Page<SalesOrder> getAll(Pageable pageable) {
        return salesOrderRepository.findAll(pageable);
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
    public void delete(Long id) {
        salesOrderRepository.deleteById(id);
    }
}
