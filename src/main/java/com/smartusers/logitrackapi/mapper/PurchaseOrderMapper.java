package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.purchaseorder.*;
import com.smartusers.logitrackapi.entity.POLine;
import com.smartusers.logitrackapi.entity.PurchaseOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
public class PurchaseOrderMapper {

    public PurchaseOrderResponse toResponse(PurchaseOrder po) {
        if (po == null) return null;

        PurchaseOrderResponse response = new PurchaseOrderResponse();
        response.setId(po.getId());
        response.setSupplierId(po.getSupplier() != null ? po.getSupplier().getId() : null);
        response.setSupplierName(po.getSupplier() != null ? po.getSupplier().getName() : null);
        response.setStatus(po.getStatus());
        response.setCreatedAt(po.getCreatedAt());


        List<PurchaseOrderLineResponse> lines = po.getLines() != null
                ? po.getLines().stream().map(this::toLineResponse).collect(Collectors.toList())
                : List.of();

        response.setLines(lines);


        double totalAmount = lines.stream()
                .mapToDouble(line -> line.getTotalPrice() != null ? line.getTotalPrice() : 0.0)
                .sum();

        response.setTotalAmount(totalAmount);

        return response;
    }

    public PurchaseOrderLineResponse toLineResponse(POLine line) {
        if (line == null) return null;

        PurchaseOrderLineResponse response = new PurchaseOrderLineResponse();
        response.setId(line.getId());
        response.setProductId(line.getProduct() != null ? line.getProduct().getId() : null);
        response.setProductName(line.getProduct() != null ? line.getProduct().getName() : null);
        response.setQuantityOrdered(line.getQuantityOrdered());
        response.setQuantityReceived(line.getQuantityReceived());

        Double unitPrice = line.getUnitPrice() != null ? line.getUnitPrice().doubleValue() : 0.0;
        response.setUnitPrice(unitPrice);

        Double totalPrice = unitPrice * (line.getQuantityOrdered() != null ? line.getQuantityOrdered() : 0);
        response.setTotalPrice(totalPrice);

        return response;
    }
}
