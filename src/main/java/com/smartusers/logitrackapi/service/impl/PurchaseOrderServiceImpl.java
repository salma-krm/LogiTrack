package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderRequest;
import com.smartusers.logitrackapi.entity.*;
import com.smartusers.logitrackapi.enums.POStatus;
import com.smartusers.logitrackapi.repository.*;
import com.smartusers.logitrackapi.service.interfaces.InventoryService;
import com.smartusers.logitrackapi.service.interfaces.PurchaseOrderService;
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
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final POLineRepository poLineRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;


    @Override
    public PurchaseOrder create(PurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));

        if (!Boolean.TRUE.equals(supplier.getActive())) {
            throw new RuntimeException("Fournisseur inactif");
        }

        // Changement ici : statut initial = RECEIVED
        PurchaseOrder po = PurchaseOrder.builder()
                .supplier(supplier)
                .status(POStatus.RECEIVED)
                .createdAt(LocalDateTime.now())
                .lines(new ArrayList<>())
                .build();

        po = purchaseOrderRepository.saveAndFlush(po);

        for (var lineReq : request.getOrderLines()) {
            Product product = productRepository.findById(lineReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            POLine line = POLine.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .quantityOrdered(lineReq.getQuantity())
                    .quantityReceived(lineReq.getQuantity()) // car déjà reçu
                    .unitPrice(lineReq.getUnitPrice())
                    .build();

            POLine savedLine = poLineRepository.save(line);
            po.getLines().add(savedLine);

            // Ajouter stock directement puisque PO = RECEIVED
            Warehouse warehouse = warehouseRepository.findAll().stream()
                    .filter(Warehouse::getActive)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Aucun entrepôt actif trouvé"));

            Inventory inventory = inventoryRepository
                    .findByWarehouse_IdAndProduct_Id(warehouse.getId(), product.getId())
                    .orElse(null);

            if (inventory != null) {
                inventoryService.addStock(
                        inventory.getId(),
                        line.getQuantityOrdered(),
                        "Réception PO #" + po.getId()
                );
            } else {
                Inventory newInventory = Inventory.builder()
                        .warehouse(warehouse)
                        .product(product)
                        .quantityOnHand(line.getQuantityOrdered())
                        .quantityReserved(0)
                        .build();
                inventoryService.create(newInventory);
            }
        }

        return purchaseOrderRepository.save(po);
    }


    @Override
    public PurchaseOrder update(Long id, PurchaseOrderRequest request) {
        PurchaseOrder po = getById(id);

        if (po.getStatus() != POStatus.DRAFT) {
            throw new RuntimeException("Impossible de modifier une commande qui n'est pas en brouillon");
        }

        // Supprimer les anciennes lignes (si موجودة)
        if (po.getLines() != null && !po.getLines().isEmpty()) {
            poLineRepository.deleteAll(po.getLines());
            po.getLines().clear();
        }

        // Ajouter les nouvelles lignes
        for (var lineReq : request.getOrderLines()) {
            Product product = productRepository.findById(lineReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            POLine line = POLine.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .quantityOrdered(lineReq.getQuantity())
                    .quantityReceived(0)
                    .unitPrice(lineReq.getUnitPrice())
                    .build();

            POLine savedLine = poLineRepository.save(line);
            po.getLines().add(savedLine);
        }

        return purchaseOrderRepository.save(po);
    }

    @Override
    public PurchaseOrder getById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande d'achat non trouvée avec ID: " + id));
    }

    @Override
    public List<PurchaseOrder> getAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public Page<PurchaseOrder> getAll(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable);
    }

    @Override
    public Page<PurchaseOrder> getBySupplierId(Long supplierId, Pageable pageable) {
        return purchaseOrderRepository.findBySupplier_Id(supplierId, pageable);
    }

    @Override
    public Page<PurchaseOrder> getByStatus(POStatus status, Pageable pageable) {
        return purchaseOrderRepository.findByStatus(status, pageable);
    }

    @Override
    public PurchaseOrder changeStatus(Long id, POStatus status) {
        PurchaseOrder po = getById(id);
        po.setStatus(status);
        return purchaseOrderRepository.save(po);
    }

    @Override
    public PurchaseOrder approve(Long id) {
        PurchaseOrder po = getById(id);
        if (po.getStatus() != POStatus.DRAFT) {
            throw new RuntimeException("Seules les commandes en brouillon peuvent être approuvées");
        }
        po.setStatus(POStatus.APPROVED);
        return purchaseOrderRepository.save(po);
    }

    @Override
    public PurchaseOrder receive(Long id) {
        PurchaseOrder po = getById(id);

        if (po.getStatus() != POStatus.APPROVED) {
            throw new RuntimeException("Seules les commandes approuvées peuvent être reçues");
        }

        Warehouse warehouse = warehouseRepository.findAll().stream()
                .filter(Warehouse::getActive)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun entrepôt actif trouvé"));

        for (POLine line : po.getLines()) {
            line.setQuantityReceived(line.getQuantityOrdered());
            poLineRepository.save(line);

            Inventory inventory = inventoryRepository
                    .findByWarehouse_IdAndProduct_Id(warehouse.getId(), line.getProduct().getId())
                    .orElse(null);

            if (inventory != null) {
                inventoryService.addStock(
                        inventory.getId(),
                        line.getQuantityOrdered(),
                        "Réception PO #" + po.getId()
                );
            } else {
                Inventory newInventory = Inventory.builder()
                        .warehouse(warehouse)
                        .product(line.getProduct())
                        .quantityOnHand(line.getQuantityOrdered())
                        .quantityReserved(0)
                        .build();
                inventoryService.create(newInventory);
            }
        }

        po.setStatus(POStatus.RECEIVED);
        return purchaseOrderRepository.save(po);
    }

    @Override
    public PurchaseOrder cancel(Long id) {
        PurchaseOrder po = getById(id);
        if (po.getStatus() == POStatus.RECEIVED) {
            throw new RuntimeException("Impossible d'annuler une commande déjà reçue");
        }
        po.setStatus(POStatus.CANCELLED);
        return purchaseOrderRepository.save(po);
    }

    @Override
    public List<PurchaseOrder> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return purchaseOrderRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public void delete(Long id) {
        PurchaseOrder po = getById(id);
        if (po.getStatus() != POStatus.DRAFT) {
            throw new RuntimeException("Seules les commandes en brouillon peuvent être supprimées");
        }
        purchaseOrderRepository.deleteById(id);
    }
}