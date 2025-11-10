package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.salesorder.*;
import com.smartusers.logitrackapi.entity.SalesOrder;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", uses = {SalesOrderLineMapper.class})
public interface SalesOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lines", ignore = true)
    @Mapping(target = "shipment", ignore = true)
    SalesOrder toEntity(SalesOrderRequest request);

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.firstName", target = "clientName")
    @Mapping(source = "lines", target = "orderLines")
    SalesOrderResponse toResponse(SalesOrder salesOrder);

    List<SalesOrderResponse> toResponseList(List<SalesOrder> salesOrders);
}
