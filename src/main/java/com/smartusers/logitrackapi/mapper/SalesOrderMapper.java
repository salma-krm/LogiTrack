package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.salesorder.*;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.entity.SalesOrderLine;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SalesOrderLineMapper.class})
public interface SalesOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "reservedAt", ignore = true)
    @Mapping(target = "shippedAt", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "shipment", ignore = true)
    SalesOrder
    toEntity(SalesOrderRequest request);
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.code", target = "warehouseCode")
    @Mapping(source = "shipment", target = "shipment")
    SalesOrderResponse toResponse(SalesOrder salesOrder);

    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "warehouse.code", target = "warehouseCode")
    @Mapping(source = "orderLines", target = "totalItems", qualifiedByName = "countOrderLines")
    SalesOrderSummaryResponse toSummaryResponse(SalesOrder salesOrder);

    List<SalesOrderResponse> toResponseList(List<SalesOrder> salesOrders);

    List<SalesOrderSummaryResponse> toSummaryResponseList(List<SalesOrder> salesOrders);

    @Named("countOrderLines")
    default Integer countOrderLines(List<SalesOrderLine> orderLines) {
        return orderLines != null ? orderLines.size() : 0;
    }

    @Mapping(source = "trackingNumber", target = "trackingNumber")
    @Mapping(source = "carrier", target = "carrier")
    @Mapping(source = "status", target = "status")
    ShipmentSummaryResponse toShipmentSummary(com.smartusers.logitrackapi.entity.Shipment shipment);
}