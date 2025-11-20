package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.dto.carrier.CarrierRequest;
import com.smartusers.logitrackapi.dto.carrier.CarrierResponse;
import com.smartusers.logitrackapi.entity.Carrier;
import com.smartusers.logitrackapi.mapper.CarrierMapper;
import com.smartusers.logitrackapi.repository.CarrierRepository;
import com.smartusers.logitrackapi.service.interfaces.CarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository carrierRepository;
    private final CarrierMapper mapper;

    @Override
    public CarrierResponse create(CarrierRequest request) {
        Carrier carrier = mapper.toEntity(request);
        carrierRepository.save(carrier);
        return mapper.toDto(carrier);
    }

    @Override
    public CarrierResponse update(Long id, CarrierRequest request) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Carrier introuvable"));

        mapper.updateCarrierFromDto(request, carrier);

        return mapper.toDto(carrierRepository.save(carrier));
    }

    @Override
    public CarrierResponse getById(Long id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Carrier introuvable"));
        return mapper.toDto(carrier);
    }

    @Override
    public List<CarrierResponse> getAll() {
        return carrierRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        carrierRepository.deleteById(id);
    }
}
