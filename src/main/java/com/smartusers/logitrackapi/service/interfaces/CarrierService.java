package com.smartusers.logitrackapi.service.interfaces;

import com.smartusers.logitrackapi.dto.carrier.CarrierRequest;
import com.smartusers.logitrackapi.dto.carrier.CarrierResponse;

import java.util.List;

public interface CarrierService {

    CarrierResponse create(CarrierRequest request);

    CarrierResponse update(Long id, CarrierRequest request);

    CarrierResponse getById(Long id);

    List<CarrierResponse> getAll();

    void delete(Long id);
}
