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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShipmentServiceImplTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private CarrierRepository carrierRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private ShipmentMapper mapper;

    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    private Carrier carrier;
    private SalesOrder order;
    private Shipment shipment;
    private ShipmentRequest shipmentRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        carrier = new Carrier();
        carrier.setId(1L);

        order = new SalesOrder();
        order.setId(1L);

        shipmentRequest = new ShipmentRequest();
        shipmentRequest.setCarrierId(1L);
        shipmentRequest.setSalesOrderId(1L);

        shipment = new Shipment();
        shipment.setId(1L);
        shipment.setCarrier(carrier);
        shipment.setSalesOrder(order);
        shipment.setStatus(ShipmentStatus.PLANNED);
    }

    @Test
    void testCreateShipment_Success() {
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(mapper.toEntity(shipmentRequest)).thenReturn(shipment);
        when(shipmentRepository.save(shipment)).thenReturn(shipment);
        when(mapper.toDto(any())).thenAnswer(inv -> {
            Shipment s = inv.getArgument(0);
            ShipmentResponse dto = new ShipmentResponse();
            dto.setId(s.getId());
            dto.setStatus(s.getStatus());
            return dto;
        });

        ShipmentResponse response = shipmentService.create(shipmentRequest);

        assertNotNull(response);
        assertEquals(ShipmentStatus.PLANNED, response.getStatus());
        verify(shipmentRepository, times(1)).save(shipment);
    }

    @Test
    void testCreateShipment_CarrierNotFound() {
        when(carrierRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> shipmentService.create(shipmentRequest));
        assertEquals("Transporteur introuvable", ex.getMessage());
    }

    @Test
    void testCreateShipment_OrderNotFound() {
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> shipmentService.create(shipmentRequest));
        assertEquals("Commande introuvable", ex.getMessage());
    }

    @Test
    void testMarkAsShipped() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDto(any())).thenAnswer(inv -> {
            Shipment s = inv.getArgument(0);
            ShipmentResponse dto = new ShipmentResponse();
            dto.setId(s.getId());
            dto.setStatus(s.getStatus());
            dto.setShippedDate(s.getShippedDate());
            return dto;
        });

        ShipmentResponse response = shipmentService.markAsShipped(1L);

        assertEquals(ShipmentStatus.IN_TRANSIT, response.getStatus());
        assertNotNull(response.getShippedDate());
        verify(shipmentRepository, times(1)).save(shipment);
    }

    @Test
    void testMarkAsShipped_ShipmentNotFound() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> shipmentService.markAsShipped(1L));
        assertEquals("Shipment introuvable", ex.getMessage());
    }

    @Test
    void testMarkAsDelivered() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDto(any())).thenAnswer(inv -> {
            Shipment s = inv.getArgument(0);
            ShipmentResponse dto = new ShipmentResponse();
            dto.setId(s.getId());
            dto.setStatus(s.getStatus());
            dto.setShippedDate(s.getShippedDate());
            dto.setDeliveredDate(s.getDeliveredDate());
            return dto;
        });

        ShipmentResponse response = shipmentService.markAsDelivered(1L);

        assertEquals(ShipmentStatus.DELIVERED, response.getStatus());
        assertNotNull(response.getDeliveredDate());
        verify(shipmentRepository, times(1)).save(shipment);
    }

    @Test
    void testGetById_Success() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(mapper.toDto(any())).thenAnswer(inv -> {
            Shipment s = inv.getArgument(0);
            ShipmentResponse dto = new ShipmentResponse();
            dto.setId(s.getId());
            dto.setStatus(s.getStatus());
            return dto;
        });

        ShipmentResponse response = shipmentService.getById(1L);

        assertNotNull(response);
        assertEquals(ShipmentStatus.PLANNED, response.getStatus());
    }

    @Test
    void testGetById_NotFound() {
        when(shipmentRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> shipmentService.getById(1L));
        assertEquals("Shipment introuvable", ex.getMessage());
    }

    @Test
    void testGetAll() {
        Shipment shipment2 = new Shipment();
        shipment2.setId(2L);

        when(shipmentRepository.findAll()).thenReturn(Arrays.asList(shipment, shipment2));
        when(mapper.toDto(any())).thenAnswer(inv -> {
            Shipment s = inv.getArgument(0);
            ShipmentResponse dto = new ShipmentResponse();
            dto.setId(s.getId());
            dto.setStatus(s.getStatus());
            return dto;
        });

        List<ShipmentResponse> list = shipmentService.getAll();

        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
    }

    @Test
    void testDelete() {
        doNothing().when(shipmentRepository).deleteById(1L);
        shipmentService.delete(1L);
        verify(shipmentRepository, times(1)).deleteById(1L);
    }
}
