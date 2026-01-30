package com.smartusers.logitrackapi.controllers;

import com.smartusers.logitrackapi.dto.carrier.CarrierRequest;
import com.smartusers.logitrackapi.dto.carrier.CarrierResponse;
import com.smartusers.logitrackapi.service.interfaces.CarrierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carriers")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class CarrierController {

    private final CarrierService carrierService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CarrierResponse create(@RequestBody CarrierRequest request) {
        log.info("Create carrier request: name={} code={}", request.getName(), request.getCode());
        CarrierResponse resp = carrierService.create(request);
        log.debug("Carrier created: id={} name={}", resp.getId(), resp.getName());
        return resp;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CarrierResponse update(@PathVariable Long id, @RequestBody CarrierRequest request) {
        log.info("Update carrier id={} name={}", id, request.getName());
        CarrierResponse resp = carrierService.update(id, request);
        log.debug("Carrier updated: id={} name={}", resp.getId(), resp.getName());
        return resp;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public CarrierResponse getById(@PathVariable Long id) {
        log.debug("Get carrier by id={}", id);
        return carrierService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public List<CarrierResponse> getAll() {
        log.debug("Get all carriers");
        return carrierService.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        log.info("Delete carrier id={}", id);
        carrierService.delete(id);
    }
}
