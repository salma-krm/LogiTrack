package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.inventory.*;
import com.smartusers.logitrackapi.dto.inventoryMovement.InventoryMovementRequest;
import com.smartusers.logitrackapi.dto.inventoryMovement.InventoryMovementResponse;
import com.smartusers.logitrackapi.entity.*;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "warehouse.id", source = "warehouseId")
    Inventory toEntity(InventoryRequest request);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "warehouseId", source = "warehouse.id")
    @Mapping(target = "warehouseName", source = "warehouse.name")
    InventoryResponse toResponse(Inventory inventory);

}
