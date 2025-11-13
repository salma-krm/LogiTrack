package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
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
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    @Override
    public PurchaseOrder create(PurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new BusinessException("Fournisseur non trouvé"));

        if (!Boolean.TRUE.equals(supplier.getActive())) {
            throw new BusinessException("Fournisseur inactif");
        }

        PurchaseOrder po = PurchaseOrder.builder()
                .supplier(supplier)
                .status(POStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .lines(new ArrayList<>())
                .build();

        po = purchaseOrderRepository.saveAndFlush(po);

        for (var lineReq : request.getOrderLines()) {
            Product product = productRepository.findById(lineReq.getProductId())
                    .orElseThrow(() -> new BusinessException("Produit non trouvé"));

            if (lineReq.getQuantity() <= 0) {
                throw new BusinessException("La quantité doit être positive");
            }

            POLine line = POLine.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .quantityOrdered(lineReq.getQuantity())
                    .quantityReceived(0)
                    .unitPrice(lineReq.getUnitPrice())
                    .build();

            poLineRepository.save(line);
            po.getLines().add(line);
        }

        return purchaseOrderRepository.save(po);
    }

    @Override
    public PurchaseOrder update(Long id, PurchaseOrderRequest request) {
        PurchaseOrder po = getById(id);

        if (po.getStatus() != POStatus.CREATED) {
            throw new BusinessException("Impossible de modifier une commande qui n'est pas en statut CREATED");
        }

        if (po.getLines() != null && !po.getLines().isEmpty()) {
            poLineRepository.deleteAll(po.getLines());
            po.getLines().clear();
        }

        for (var lineReq : request.getOrderLines()) {
            Product product = productRepository.findById(lineReq.getProductId())
                    .orElseThrow(() -> new BusinessException("Produit non trouvé"));

            POLine line = POLine.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .quantityOrdered(lineReq.getQuantity())
                    .quantityReceived(0)
                    .unitPrice(lineReq.getUnitPrice())
                    .build();

            poLineRepository.save(line);
            po.getLines().add(line);
        }

        return purchaseOrderRepository.save(po);
    }

    @Override
    public PurchaseOrder approve(Long id) {
        PurchaseOrder po = getById(id);

        if (po.getStatus() != POStatus.CREATED) {
            throw new BusinessException("Seules les commandes en statut CREATED peuvent être approuvées. Status actuel: " + po.getStatus());
        }

        po.setStatus(POStatus.APPROVED);
        return purchaseOrderRepository.save(po);
    }


    @Override
    public PurchaseOrder receive(Long id, Long warehouseId) {
        PurchaseOrder po = getById(id);

        if (po.getStatus() != POStatus.APPROVED) {
            throw new BusinessException("Seules les commandes approuvées peuvent être reçues");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new BusinessException("Entrepôt non trouvé"));

        if (!Boolean.TRUE.equals(warehouse.getActive())) {
            throw new BusinessException("L'entrepôt sélectionné est inactif");
        }

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
                        "Réception commande achat #" + po.getId()
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
            throw new BusinessException("Impossible d'annuler une commande déjà reçue");
        }

        po.setStatus(POStatus.CANCELLED);
        return purchaseOrderRepository.save(po);
    }

    @Override
    public PurchaseOrder getById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Commande d'achat non trouvée avec ID: " + id));
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
    public void delete(Long id) {
        PurchaseOrder po = getById(id);

        if (po.getStatus() != POStatus.CREATED && po.getStatus() != POStatus.CANCELLED) {
            throw new BusinessException("Seules les commandes en statut CREATED ou CANCELLED peuvent être supprimées");
        }

        purchaseOrderRepository.deleteById(id);
    }
}
