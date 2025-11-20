package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.carrier.CarrierRequest;
import com.smartusers.logitrackapi.dto.carrier.CarrierResponse;
import com.smartusers.logitrackapi.service.interfaces.CarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carriers")
@RequiredArgsConstructor
public class CarrierController {

    private final CarrierService carrierService;

    @PostMapping
    public CarrierResponse create(@RequestBody CarrierRequest request) {
        return carrierService.create(request);
    }

    @PutMapping("/{id}")
    public CarrierResponse update(@PathVariable Long id, @RequestBody CarrierRequest request) {
        return carrierService.update(id, request);
    }

    @GetMapping("/{id}")
    public CarrierResponse getById(@PathVariable Long id) {
        return carrierService.getById(id);
    }

    @GetMapping
    public List<CarrierResponse> getAll() {
        return carrierService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carrierService.delete(id);
    }
}
