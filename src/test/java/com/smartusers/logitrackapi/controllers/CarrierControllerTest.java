package com.smartusers.logitrackapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartusers.logitrackapi.dto.carrier.CarrierRequest;
import com.smartusers.logitrackapi.dto.carrier.CarrierResponse;
import com.smartusers.logitrackapi.service.interfaces.CarrierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CarrierControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CarrierService carrierService;

    @InjectMocks
    private CarrierController carrierController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(carrierController).build();
    }

    @Test
    void testCreateCarrier() throws Exception {
        CarrierRequest request = new CarrierRequest();
        request.setName("Carrier1");
        request.setCode("C1");

        CarrierResponse response = new CarrierResponse();
        response.setName("Carrier1");
        response.setCode("C1");

        when(carrierService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/carriers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carrier1"));
    }

    @Test
    void testGetAllCarriers() throws Exception {
        CarrierResponse carrier1 = new CarrierResponse();
        carrier1.setName("Carrier1");
        CarrierResponse carrier2 = new CarrierResponse();
        carrier2.setName("Carrier2");

        when(carrierService.getAll()).thenReturn(List.of(carrier1, carrier2));

        mockMvc.perform(get("/api/carriers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }
}
