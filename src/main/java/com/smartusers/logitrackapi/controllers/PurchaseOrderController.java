package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderRequest;
import com.smartusers.logitrackapi.dto.purchaseorder.PurchaseOrderResponse;
import com.smartusers.logitrackapi.entity.PurchaseOrder;
import com.smartusers.logitrackapi.enums.POStatus;
import com.smartusers.logitrackapi.mapper.PurchaseOrderMapper;
import com.smartusers.logitrackapi.service.interfaces.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderMapper purchaseOrderMapper;

    // 🟢 Créer un bon de commande
    @PostMapping
    public PurchaseOrderResponse create(@Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrder po = purchaseOrderService.create(request);
        return purchaseOrderMapper.toResponse(po);
    }

    // 🟢 Modifier un bon de commande
    @PutMapping("/{id}")
    public PurchaseOrderResponse update(@PathVariable Long id, @Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrder po = purchaseOrderService.update(id, request);
        return purchaseOrderMapper.toResponse(po);
    }

    // 🟢 Récupérer toutes les commandes (liste simple)
    @GetMapping
    public List<PurchaseOrderResponse> getAll() {
        return purchaseOrderService.getAll()
                .stream()
                .map(purchaseOrderMapper::toResponse)
                .toList();
    }

    // 🟢 Récupérer une commande par ID
    @GetMapping("/{id}")
    public PurchaseOrderResponse getById(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderService.getById(id);
        return purchaseOrderMapper.toResponse(po);
    }

    // 🟢 Supprimer une commande
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
    }

    // 🟢 Approuver une commande
    @PatchMapping("/{id}/approve")
    public PurchaseOrderResponse approve(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderService.approve(id);
        return purchaseOrderMapper.toResponse(po);
    }

    // 🟢 Réceptionner une commande
    @PatchMapping("/{id}/receive")
    public PurchaseOrderResponse receive(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderService.receive(id);
        return purchaseOrderMapper.toResponse(po);
    }

    // 🟢 Annuler une commande
    @PatchMapping("/{id}/cancel")
    public PurchaseOrderResponse cancel(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderService.cancel(id);
        return purchaseOrderMapper.toResponse(po);
    }

    // 🟢 Changer le statut manuellement
    @PatchMapping("/{id}/status/{status}")
    public PurchaseOrderResponse changeStatus(@PathVariable Long id, @PathVariable POStatus status) {
        PurchaseOrder po = purchaseOrderService.changeStatus(id, status);
        return purchaseOrderMapper.toResponse(po);
    }

    // 🟢 Rechercher les commandes entre deux dates
    @GetMapping("/between")
    public List<PurchaseOrderResponse> getOrdersBetweenDates(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);

        return purchaseOrderService.getOrdersBetweenDates(startDate, endDate)
                .stream()
                .map(purchaseOrderMapper::toResponse)
                .toList();
    }

    // 🟢 Filtrer par fournisseur
    @GetMapping("/supplier/{supplierId}")
    public Page<PurchaseOrderResponse> getBySupplierId(@PathVariable Long supplierId, Pageable pageable) {
        return purchaseOrderService.getBySupplierId(supplierId, pageable)
                .map(purchaseOrderMapper::toResponse);
    }

    // 🟢 Filtrer par statut
    @GetMapping("/status/{status}")
    public Page<PurchaseOrderResponse> getByStatus(@PathVariable POStatus status, Pageable pageable) {
        return purchaseOrderService.getByStatus(status, pageable)
                .map(purchaseOrderMapper::toResponse);
    }
}
