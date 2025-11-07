package com.smartusers.logitrackapi.dto.warehouse;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseRequest {
    @NotBlank(message = "Le nom du warehouse est obligatoire")
    private String name;

    private Boolean active = true;
}
