package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.carrier.CarrierRequest;
import com.smartusers.logitrackapi.dto.carrier.CarrierResponse;
import com.smartusers.logitrackapi.entity.Carrier;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CarrierMapper {

    Carrier toEntity(CarrierRequest request);

    CarrierResponse toDto(Carrier carrier);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCarrierFromDto(CarrierRequest request, @MappingTarget Carrier carrier);
}
