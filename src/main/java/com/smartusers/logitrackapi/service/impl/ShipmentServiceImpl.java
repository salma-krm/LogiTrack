package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.dto.shipment.ShipmentRequest;
import com.smartusers.logitrackapi.dto.shipment.ShipmentResponse;
import com.smartusers.logitrackapi.entity.Carrier;
import com.smartusers.logitrackapi.entity.SalesOrder;
import com.smartusers.logitrackapi.entity.Shipment;
import com.smartusers.logitrackapi.enums.ShipmentStatus;
import com.smartusers.logitrackapi.mapper.ShipmentMapper;
import com.smartusers.logitrackapi.repository.CarrierRepository;
import com.smartusers.logitrackapi.repository.SalesOrderRepository;
import com.smartusers.logitrackapi.repository.ShipmentRepository;
import com.smartusers.logitrackapi.service.interfaces.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final CarrierRepository carrierRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ShipmentMapper mapper;

    @Override
    public ShipmentResponse create(ShipmentRequest request) {

        Shipment shipment = mapper.toEntity(request);

        Carrier carrier = carrierRepository.findById(request.getCarrierId())
                .orElseThrow(() -> new BusinessException("Transporteur introuvable"));

        SalesOrder order = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new BusinessException("Commande introuvable"));

        shipment.setCarrier(carrier);
        shipment.setSalesOrder(order);

        // Status initial correct
        shipment.setStatus(ShipmentStatus.PLANNED);
        shipment.setShippedDate(null);
        shipment.setDeliveredDate(null);

        shipmentRepository.save(shipment);

        return mapper.toDto(shipment);
    }

    @Override
    public ShipmentResponse markAsShipped(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Shipment introuvable"));

        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setShippedDate(LocalDateTime.now());

        return mapper.toDto(shipmentRepository.save(shipment));
    }

    @Override
    public ShipmentResponse markAsDelivered(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Shipment introuvable"));

        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredDate(LocalDateTime.now());

        return mapper.toDto(shipmentRepository.save(shipment));
    }

    @Override
    public ShipmentResponse getById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Shipment introuvable"));
        return mapper.toDto(shipment);
    }

    @Override
    public List<ShipmentResponse> getAll() {
        return shipmentRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        shipmentRepository.deleteById(id);
    }
}
