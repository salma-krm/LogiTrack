package com.smartusers.logitrackapi.mapper;


import com.smartusers.logitrackapi.dto.warehouse.WarehouseRequest;
import com.smartusers.logitrackapi.dto.warehouse.WarehouseResponse;
import com.smartusers.logitrackapi.entity.Warehouse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inventories", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Warehouse toEntity(WarehouseRequest request);


    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(source = "manager.firstName", target = "managerName")
    @Mapping(source = "manager.email", target = "managerEmail")
    WarehouseResponse toResponse(Warehouse warehouse);
}
