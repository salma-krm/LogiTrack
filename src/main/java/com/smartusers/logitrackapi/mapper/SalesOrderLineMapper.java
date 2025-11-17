package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.salesorder.*;
import com.smartusers.logitrackapi.entity.SalesOrderLine;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalesOrderLineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "salesOrder", ignore = true)
    @Mapping(target = "qtyReserved", ignore = true)
    SalesOrderLine toEntity(SalesOrderLineRequest request);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.sku", target = "productSku")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "qtyOrdered", target = "qtyOrdered")
    @Mapping(source = "qtyReserved", target = "qtyReserved")
    @Mapping(source = "price", target = "price")
    SalesOrderLineResponse toResponse(SalesOrderLine salesOrderLine);

    List<SalesOrderLineResponse> toResponseList(List<SalesOrderLine> salesOrderLines);
}

