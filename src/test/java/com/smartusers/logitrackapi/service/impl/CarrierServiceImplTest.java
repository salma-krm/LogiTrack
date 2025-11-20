package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.dto.carrier.CarrierRequest;
import com.smartusers.logitrackapi.dto.carrier.CarrierResponse;
import com.smartusers.logitrackapi.entity.Carrier;
import com.smartusers.logitrackapi.mapper.CarrierMapper;
import com.smartusers.logitrackapi.repository.CarrierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarrierServiceImplTest {

    @Mock
    private CarrierRepository carrierRepository;

    @Mock
    private CarrierMapper mapper;

    @InjectMocks
    private CarrierServiceImpl carrierService;

    private Carrier carrier;
    private CarrierRequest request;
    private CarrierResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carrier = new Carrier();
        carrier.setId(1L);
        carrier.setName("Carrier A");
        carrier.setCode("C001");

        request = new CarrierRequest();
        request.setName("Carrier A");
        request.setCode("C001");

        response = new CarrierResponse();
        response.setId(1L);
        response.setName("Carrier A");
        response.setCode("C001");
    }

    @Test
    void testCreateCarrier() {
        when(mapper.toEntity(request)).thenReturn(carrier);
        when(carrierRepository.save(carrier)).thenReturn(carrier);
        when(mapper.toDto(carrier)).thenReturn(response);

        CarrierResponse result = carrierService.create(request);

        assertNotNull(result);
        assertEquals("Carrier A", result.getName());
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void testUpdateCarrier() {
        CarrierRequest updateRequest = new CarrierRequest();
        updateRequest.setName("Carrier B");
        updateRequest.setCode("C002");

        Carrier updatedCarrier = new Carrier();
        updatedCarrier.setId(1L);
        updatedCarrier.setName("Carrier B");
        updatedCarrier.setCode("C002");

        CarrierResponse updatedResponse = new CarrierResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Carrier B");
        updatedResponse.setCode("C002");

        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        doAnswer(invocation -> {
            carrier.setName("Carrier B");
            carrier.setCode("C002");
            return null;
        }).when(mapper).updateCarrierFromDto(updateRequest, carrier);
        when(carrierRepository.save(carrier)).thenReturn(carrier);
        when(mapper.toDto(carrier)).thenReturn(updatedResponse);

        CarrierResponse result = carrierService.update(1L, updateRequest);

        assertEquals("Carrier B", result.getName());
        assertEquals("C002", result.getCode());
        verify(carrierRepository, times(1)).save(carrier);
    }

    @Test
    void testUpdateCarrierNotFound() {
        when(carrierRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> carrierService.update(1L, request));
        assertEquals("Carrier introuvable", ex.getMessage());
    }

    @Test
    void testGetById() {
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        when(mapper.toDto(carrier)).thenReturn(response);

        CarrierResponse result = carrierService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(carrierRepository, times(1)).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        when(carrierRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> carrierService.getById(1L));
        assertEquals("Carrier introuvable", ex.getMessage());
    }

    @Test
    void testGetAll() {
        Carrier c2 = new Carrier();
        c2.setId(2L);
        c2.setName("Carrier B");
        c2.setCode("C002");

        CarrierResponse r2 = new CarrierResponse();
        r2.setId(2L);
        r2.setName("Carrier B");
        r2.setCode("C002");

        when(carrierRepository.findAll()).thenReturn(Arrays.asList(carrier, c2));
        when(mapper.toDto(carrier)).thenReturn(response);
        when(mapper.toDto(c2)).thenReturn(r2);

        List<CarrierResponse> result = carrierService.getAll();

        assertEquals(2, result.size());
        verify(carrierRepository, times(1)).findAll();
    }

    @Test
    void testDeleteCarrier() {
        doNothing().when(carrierRepository).deleteById(1L);

        carrierService.delete(1L);

        verify(carrierRepository, times(1)).deleteById(1L);
    }
}
