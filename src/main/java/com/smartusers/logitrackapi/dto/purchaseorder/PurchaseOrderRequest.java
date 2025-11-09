package com.smartusers.logitrackapi.dto.purchaseorder;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PurchaseOrderRequest {
    @NotNull(message = "L'ID du fournisseur est obligatoire")
    private Long supplierId;

    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    private List<PurchaseOrderLineRequest> orderLines;
}

