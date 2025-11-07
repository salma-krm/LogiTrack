package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.salesorder.SalesOrderLineRequest;
import com.smartusers.logitrackapi.dto.salesorder.SalesOrderLineResponse;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.entity.SalesOrderLine;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalesOrderLineMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "salesOrder", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "quantityReserved", ignore = true)
    @Mapping(target = "quantityShipped", ignore = true)
    SalesOrderLine toEntity(SalesOrderLineRequest request);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.sku", target = "productSku")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = ".", target = "pendingQuantity", qualifiedByName = "calculatePendingQuantity")
    SalesOrderLineResponse toResponse(SalesOrderLine salesOrderLine);

    List<SalesOrderLineResponse> toResponseList(List<SalesOrderLine> salesOrderLines);

    @Named("calculatePendingQuantity")
    default Integer calculatePendingQuantity(SalesOrderLine line) {
        return line.getPendingQuantity();
    }

    @AfterMapping
    default void linkOrderLines(@MappingTarget SalesOrder salesOrder) {
        if (salesOrder.getOrderLines() != null) {
            salesOrder.getOrderLines().forEach(line -> line.setSalesOrder(salesOrder));
        }
    }
}