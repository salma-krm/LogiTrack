package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.shipment.ShipmentRequest;
import com.smartusers.logitrackapi.dto.shipment.ShipmentResponse;
import com.smartusers.logitrackapi.entity.Shipment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {

    @Mapping(target = "carrierId", source = "carrier.id")
    @Mapping(target = "salesOrderId", source = "salesOrder.id")
    ShipmentResponse toDto(Shipment shipment);

    @Mapping(target = "carrier", ignore = true)
    @Mapping(target = "salesOrder", ignore = true)
    Shipment toEntity(ShipmentRequest request);
}
