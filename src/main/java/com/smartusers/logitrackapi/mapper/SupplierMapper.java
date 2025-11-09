package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.supplier.SupplierRequest;
import com.smartusers.logitrackapi.dto.supplier.SupplierResponse;
import com.smartusers.logitrackapi.entity.Supplier;
import org.mapstruct.*;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface SupplierMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrders", ignore = true)
    @Mapping(target = "active", expression = "java(request.getActive() != null ? request.getActive() : true)")
    Supplier toEntity(SupplierRequest request);


    @Mapping(target = "totalPurchaseOrders",
            expression = "java(supplier.getPurchaseOrders() != null ? supplier.getPurchaseOrders().size() : 0)")
    SupplierResponse toResponse(Supplier supplier);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(SupplierRequest request, @MappingTarget Supplier supplier);
}
