package com.smartusers.logitrackapi.dto.supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierRequest {

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;

    @NotBlank(message = "Les informations de contact sont obligatoires")
    @Size(max = 255, message = "Les informations de contact ne peuvent pas dépasser 255 caractères")
    private String contactInfo;


    private Boolean active = true;
}
